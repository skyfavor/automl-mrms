package com.seculayer.mrms.kubernetes.yaml.job;

import com.seculayer.mrms.common.Constants;
import com.seculayer.mrms.info.InfoAbstract;
import com.seculayer.mrms.kubernetes.KubeUtil;
import com.seculayer.mrms.kubernetes.yaml.configmap.KubeConfigMap;
import com.seculayer.mrms.kubernetes.yaml.container.DAContainer;
import com.seculayer.mrms.kubernetes.yaml.container.MLPSContainer;
import com.seculayer.mrms.kubernetes.yaml.container.RcmdContainer;
import io.kubernetes.client.openapi.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class KubeJob {
    private Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    protected List<KubeConfigMap> configMapList;

    protected String metaname;
    protected Map<String, String> labels;
    protected Map<String, String> nodeSelector;
    protected List<V1Container> containers;
    protected List<V1Volume> volumes;

    protected int workerIdx = 0;

    public KubeJob(){
        this.configMapList = KubeUtil.getConfMapList(
                Constants.KUBE_EYECLOUDAI_NAMESPACE, this.makeConfigMapName());

    }

    public KubeJob workerIdx(int workerIdx){
        this.workerIdx = workerIdx;
        return this;
    }

    protected InfoAbstract info = null;

    public KubeJob info(InfoAbstract info){
        this.info = info;
        return this;
    }

    protected String jobType;

    public KubeJob jobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    protected V1Job makeJob(){
        long userNum = 1000;
        logger.debug("-------------------------------------------");
        logger.debug("In KubeJob..");
        logger.debug("metaname : {}", this.metaname);
        logger.debug("labels : {}", this.labels.toString());
        logger.debug("containers : {}, cnt : {}", this.containers.toString(), this.containers.size());
        logger.debug("volumes : {}, cnt : {}", this.volumes.toString(), this.volumes.size());
        logger.debug("-------------------------------------------");

        return new V1Job()
                .apiVersion("batch/v1")
                .kind("Job")
                .metadata(new V1ObjectMeta().name(this.metaname).labels(this.labels))
                .spec(new V1JobSpec()
                        .template(new V1PodTemplateSpec()
                                .metadata(new V1ObjectMeta().labels(this.labels))
                                .spec(new V1PodSpec()
                                        .securityContext(new V1PodSecurityContext().fsGroup(userNum))
                                        .containers(this.containers)
                                        .restartPolicy("Never")
                                        //.nodeSelector(this.makeNodeSelector())
                                        .volumes(this.volumes))));

    }

    protected abstract List<String> makeConfigMapName();
    public abstract V1Job make();
    protected abstract Map<String, String> makeLabels();
    protected abstract List<V1Container> makeContainers();
    protected List<V1Volume> makeVolumes(){ return KubeUtil.volumes(); }

    // ml common kubernetes objects
    protected List<V1Container> daContainers(String jobType){
        List<V1Container> containers = new ArrayList<>();
        containers.add(
                new DAContainer(jobType)
                .info(this.info)
                .workerIdx(workerIdx)
                .configMapList(this.configMapList)
                .make()
        );

        return containers;
    }

    protected List<V1Container> rcmdContainers(String jobType){
        List<V1Container> containers = new ArrayList<>();
        containers.add(
            new RcmdContainer(jobType)
                .info(this.info)
                .workerIdx(workerIdx)
                .configMapList(this.configMapList)
                .make()
        );

        return containers;
    }

    protected List<V1Container> mlpsContainers(String jobType){
        List<V1Container> containers = new ArrayList<>();
        containers.add(
            new MLPSContainer(jobType)
                .info(this.info)
                .workerIdx(workerIdx)
                .configMapList(this.configMapList)
                .make()
        );

        return containers;
    }
}
