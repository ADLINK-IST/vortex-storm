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

import java.io.Serializable;

public class VortexConfig<TYPE> implements Serializable {
    private static final long serialVersionUID = -8615550364799516353L;

    private int                   domain;
    private String                topicName;
    private Class<TYPE>           topicType;
    private String                topicRegType;
    private SampleBuilder<TYPE> sampleBuilder;

    private String[]              fields;

    private int                   queueSize;

//    private List<QosPolicy.ForDataReader>   drQos;
//    private List<QosPolicy.ForDataWriter>   dwQos;
//    private List<QosPolicy.ForTopic>        topicQos;

    private Class<PolicyConfigurator<QosPolicy.ForDataReader>> drQos;
    private Class<PolicyConfigurator<QosPolicy.ForDataWriter>> dwQos;
    private Class<PolicyConfigurator<QosPolicy.ForTopic>> topicQos;

    public VortexConfig(int domain, String topicName, Class<TYPE> topicType, String topicRegType, SampleBuilder<TYPE> sampleBuilder, String[] fields, int queueSize) {
        this.domain = domain;
        this.topicName = topicName;
        this.topicType = topicType;
        this.topicRegType = topicRegType;
        this.sampleBuilder = sampleBuilder;
        this.queueSize = queueSize;
        this.fields = fields;
    }

    public VortexConfig() {
        this(0, "", null, "", null, new String[0], 100);
    }

    public int getDomain() {
        return domain;
    }

    public void setDomain(int newDomain) {
        domain = newDomain;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String newTopicName) {
        topicName = newTopicName;
    }

    public Class<TYPE> getTopicType() {
        return topicType;
    }

    public void setTopicType(Class<TYPE> newTopicType) {
        topicType = newTopicType;
    }

    public String getTopicRegType() {
        return topicRegType;
    }

    public void setTopicRegType(String newTopicRegType) {
        topicRegType = newTopicRegType;
    }

    public SampleBuilder<TYPE> getSampleBuilder() {
        return sampleBuilder;
    }

    public void setSampleBuilder(SampleBuilder<TYPE> newSampleBuilder) {
        sampleBuilder = newSampleBuilder;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int newQueueSize) {
        queueSize = newQueueSize;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] newFields) {
        this.fields = newFields;
    }

    public Class<PolicyConfigurator<QosPolicy.ForTopic>> getTopicQos() {
        return topicQos;
    }

    public void setTopicQos(Class<? extends PolicyConfigurator<QosPolicy.ForTopic>> topicQos) {
        this.topicQos = (Class<PolicyConfigurator<QosPolicy.ForTopic>>) topicQos;
    }

    public Class<PolicyConfigurator<QosPolicy.ForDataWriter>> getDwQos() {
        return dwQos;
    }

    public void setDwQos(Class<? extends PolicyConfigurator<QosPolicy.ForDataWriter>> dwQos) {
        this.dwQos = (Class<PolicyConfigurator<QosPolicy.ForDataWriter>>) dwQos;
    }

    public Class<PolicyConfigurator<QosPolicy.ForDataReader>> getDrQos() {
        return drQos;
    }

    public void setDrQos(Class<? extends PolicyConfigurator<QosPolicy.ForDataReader>> drQos) {
        this.drQos = (Class<PolicyConfigurator<QosPolicy.ForDataReader>>) drQos;
    }

}
