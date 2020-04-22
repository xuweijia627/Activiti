package org.activiti.engine.impl.cmd;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.deploy.DeploymentManager;
import org.activiti.engine.impl.runtime.ProcessInstanceBuilderImpl;
import org.activiti.engine.impl.util.ProcessDefinitionRetriever;
import org.activiti.engine.impl.util.ProcessInstanceHelper;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.Map;

public class CreateProcessInstanceCmd implements Command<ProcessInstance> {

    private static final long serialVersionUID = 1L;
    protected String processDefinitionKey;
    protected String processDefinitionId;
    protected Map<String, Object> variables;
    protected Map<String, Object> transientVariables;
    protected String businessKey;
    protected String tenantId;
    protected String processInstanceName;
    protected ProcessInstanceHelper processInstanceHelper;

    public CreateProcessInstanceCmd(String processDefinitionKey, String processDefinitionId, String businessKey, Map<String, Object> variables) {
        this.processDefinitionKey = processDefinitionKey;
        this.processDefinitionId = processDefinitionId;
        this.businessKey = businessKey;
        this.variables = variables;
    }

    public CreateProcessInstanceCmd(String processDefinitionKey, String processDefinitionId, String businessKey, Map<String, Object> variables, String tenantId) {
        this(processDefinitionKey, processDefinitionId, businessKey, variables);
        this.tenantId = tenantId;
    }

    public CreateProcessInstanceCmd(ProcessInstanceBuilderImpl processInstanceBuilder) {
        this(processInstanceBuilder.getProcessDefinitionKey(),
            processInstanceBuilder.getProcessDefinitionId(),
            processInstanceBuilder.getBusinessKey(),
            processInstanceBuilder.getVariables(),
            processInstanceBuilder.getTenantId());
        this.processInstanceName = processInstanceBuilder.getProcessInstanceName();
        this.transientVariables = processInstanceBuilder.getTransientVariables();
    }

    public ProcessInstance execute(CommandContext commandContext) {

        DeploymentManager deploymentCache = commandContext.getProcessEngineConfiguration().getDeploymentManager();

        ProcessDefinitionRetriever processRetriever = new ProcessDefinitionRetriever(this.tenantId, deploymentCache);
        ProcessDefinition processDefinition = processRetriever.getProcessDefinition(this.processDefinitionId, this.processDefinitionKey);

        processInstanceHelper = commandContext.getProcessEngineConfiguration().getProcessInstanceHelper();
        return processInstanceHelper.createProcessInstance(processDefinition, businessKey, processInstanceName, variables, transientVariables);
    }


}
