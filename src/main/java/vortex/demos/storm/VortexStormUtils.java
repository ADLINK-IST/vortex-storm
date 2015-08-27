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
package vortex.demos.storm;

import org.omg.dds.core.policy.QosPolicy;
import org.omg.dds.domain.DomainParticipant;
import org.omg.dds.pub.DataWriterQos;
import org.omg.dds.pub.Publisher;
import org.omg.dds.sub.DataReaderQos;
import org.omg.dds.sub.Subscriber;
import org.omg.dds.topic.TopicQos;

public class VortexStormUtils {
    public static DataWriterQos getDataWriterQos(VortexConfig<?> config, Publisher pub) {
        DataWriterQos dwQos = pub.getDefaultDataWriterQos();
        try {
            final Class<PolicyConfigurator<QosPolicy.ForDataWriter>> dwQosCls = config.getDwQos();
            if(dwQosCls != null) {
                final PolicyConfigurator<QosPolicy.ForDataWriter> dwPolicyConfigurator = dwQosCls.newInstance();
                if(dwPolicyConfigurator != null) {
                    dwQos = dwQos.withPolicies(dwPolicyConfigurator.get());
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return dwQos;
    }

    public static DataReaderQos getDataReaderQos(VortexConfig<?> config, Subscriber sub) {
        DataReaderQos drQos = sub.getDefaultDataReaderQos();
        try {
            final Class<PolicyConfigurator<QosPolicy.ForDataReader>> drQosCls = config.getDrQos();
            if(drQosCls != null) {
                final PolicyConfigurator<QosPolicy.ForDataReader> drPolicyConfigurator = drQosCls.newInstance();
                if(drPolicyConfigurator != null) {
                    drQos = drQos.withPolicies(drPolicyConfigurator.get());
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return drQos;
    }

    public static TopicQos getTopicQos(VortexConfig<?> config, DomainParticipant participant) {
        TopicQos topicQos = participant.getDefaultTopicQos();
        try {
            final Class<PolicyConfigurator<QosPolicy.ForTopic>> topicQosCls = config.getTopicQos();
            if(topicQosCls != null) {
                final PolicyConfigurator<QosPolicy.ForTopic> topicPolicyConfigurator = topicQosCls.newInstance();
                if(topicPolicyConfigurator != null) {
                    topicQos = topicQos.withPolicies(topicPolicyConfigurator.get());
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return topicQos;
    }
}
