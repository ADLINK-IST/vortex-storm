/**
 * PrismTech licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License and with the PrismTech Vortex product. You may obtain a copy of the
 * License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License and README for the specific language governing permissions and
 * limitations under the License.
 */
package vortex.demos.storm.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import org.omg.dds.core.event.DataAvailableEvent;
import org.omg.dds.core.status.Status;
import org.omg.dds.sub.DataReader;
import org.omg.dds.sub.DataReaderQos;
import org.omg.dds.sub.Sample;
import org.omg.dds.sub.SampleState;
import org.omg.dds.topic.Topic;
import org.omg.dds.topic.TopicQos;
import org.omg.dds.type.TypeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vortex.commons.util.BaseDataReaderListener;
import vortex.commons.util.VConfig;
import vortex.demos.storm.VortexConfig;
import vortex.demos.storm.VortexStormUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static vortex.commons.util.VConfig.DefaultEntities.defaultDomainParticipant;
import static vortex.commons.util.VConfig.DefaultEntities.defaultSub;


public class VortexSpout<TYPE> extends BaseRichSpout {
    static final long serialVersionUID = 737015318988609460L;

    static final Logger LOG = LoggerFactory.getLogger(VortexSpout.class);

    SpoutOutputCollector collector;

    VortexConfig<TYPE> config;

    DataReader<TYPE>    dr;

    LinkedBlockingQueue<Sample<TYPE>> queue;


    public VortexSpout(VortexConfig<TYPE> config) {
        this.config = config;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(config.getFields()));
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        queue = new LinkedBlockingQueue<Sample<TYPE>>(config.getQueueSize());
        configDataReader();
        startListener();
    }

    @Override
    public void close() {
        stopListener();
        dr.close();
        super.close();
    }

    @Override
    public void nextTuple() {
        Sample<TYPE> sample;
        while((sample = queue.poll()) != null) {
            collector.emit(config.getSampleBuilder().deSerialize(sample));
        }
    }

    private void configDataReader() {
        final TypeSupport<TYPE> typeSupport = TypeSupport.newTypeSupport(config.getTopicType(), config.getTopicRegType(), VConfig.ENV);

        TopicQos topicQos = VortexStormUtils.getTopicQos(config, defaultDomainParticipant());

        DataReaderQos drQos = VortexStormUtils.getDataReaderQos(config, defaultSub());

        final Topic<TYPE> topic =
                defaultDomainParticipant().createTopic(config.getTopicName(), typeSupport, topicQos, null, (Collection<Class<? extends Status>>) null);
        dr = defaultSub().createDataReader(topic, drQos);
    }

    private void startListener() {
        dr.setListener(new BaseDataReaderListener<TYPE>() {
            @Override
            public void onDataAvailable(DataAvailableEvent<TYPE> dataAvailableEvent) {
                final DataReader.Selector<TYPE> selector = dr.select();
                selector.dataState(selector.getDataState().with(SampleState.NOT_READ));
                final Sample.Iterator<TYPE> samples = dr.take(selector);
                try {
                    while(samples.hasNext()){
                        final Sample<TYPE> sample = samples.next();
                        TYPE data = sample.getData();
                        if(data != null) {
                            queue.offer(sample);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void stopListener() {
        dr.setListener(null);
    }
}
