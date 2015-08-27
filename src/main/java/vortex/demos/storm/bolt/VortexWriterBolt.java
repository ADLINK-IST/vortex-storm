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
package vortex.demos.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import org.omg.dds.core.status.Status;
import org.omg.dds.pub.DataWriter;
import org.omg.dds.pub.DataWriterQos;
import org.omg.dds.topic.Topic;
import org.omg.dds.topic.TopicQos;
import org.omg.dds.type.TypeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vortex.commons.util.VConfig;
import vortex.demos.storm.VortexConfig;
import vortex.demos.storm.VortexStormUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static vortex.commons.util.VConfig.DefaultEntities.defaultDomainParticipant;
import static vortex.commons.util.VConfig.DefaultEntities.defaultPub;

public class VortexWriterBolt<TYPE> extends BaseRichBolt {
    private static final int DS_HISTORY                 = 100;
    private static final int DS_MAX_SAMPLES             = 8192;
    private static final int DS_MAX_INSTANCES           = 4196;
    private static final int DS_MAX_SAMPLES_X_INSTANCE  = 8192;
    private static final int DS_CLEAN_UP_DELAY          = 3600;

    private static final long   serialVersionUID = 7155814554135054229L;
    private static final Logger LOG = LoggerFactory.getLogger(VortexWriterBolt.class);

    private OutputCollector     collector;

    private VortexConfig<TYPE>   config;
    private DataWriter<TYPE>     dw;

    public VortexWriterBolt(VortexConfig<TYPE> config) {
        this.config = config;
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;

        configDataWriter();
    }

    @Override
    public void execute(Tuple tuple) {
        final TYPE sample = config.getSampleBuilder().serialize(tuple);
        if(sample != null) {
            try {
                dw.write(sample);
            } catch (TimeoutException e) {
                LOG.error("Unable to write sample.", e);
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        return;
    }

    @Override
    public void cleanup() {
        super.cleanup();

        if(dw != null) {
            dw.close();
        }
    }

    private void configDataWriter() {
        if(dw == null) {
            final TypeSupport<TYPE> typeSupport = TypeSupport.newTypeSupport(config.getTopicType(),
                    config.getTopicRegType(),
                    VConfig.ENV);

            TopicQos topicQos = VortexStormUtils.getTopicQos(config, defaultDomainParticipant());

            DataWriterQos dwQos = VortexStormUtils.getDataWriterQos(config, defaultPub());

            final Topic<TYPE> topic = defaultDomainParticipant()
                    .createTopic(config.getTopicName(), typeSupport, topicQos, null, (Collection<Class<? extends Status>>) null);
            dw = defaultPub().createDataWriter(topic);
        }
    }
}
