package org.rundeck.app.data.providers

import grails.compiler.GrailsCompileStatic
import groovy.util.logging.Slf4j
import org.rundeck.app.data.model.v1.project.RdProject
import org.rundeck.app.data.providers.v1.project.RundeckProjectDataProvider
import org.rundeck.spi.data.DataAccessException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import rundeck.Project
import rundeck.services.data.ProjectDataService

import javax.transaction.Transactional

@GrailsCompileStatic
@Slf4j
@Transactional
class GormProjectDataProvider implements RundeckProjectDataProvider {

    @Autowired
    ProjectDataService projectDataService

    @Autowired
    MessageSource messageSource

    @Override
    RdProject getData (final Serializable id) {
        RdProject project = projectDataService.get(id)
        return project ?: null
    }

    Long create(RdProject data) throws DataAccessException {
        Project project = new Project(
                name: data.getName(),
                description: data.getDescription(), 
                state: data.getState())
        try {
            if (projectDataService.save(project)) {
                return project.getId()
            } else {
                log.warn(project.errors.allErrors.collect { messageSource.getMessage(it, null) }.join(","))
                throw new DataAccessException("Failed to create project: ${data.name}")
            }
        } catch (Exception e) {
            throw new DataAccessException("Failed to create project: ${data.name}: ${e}", e)
        }
    }

    @Override
    void update(final Serializable id, final RdProject data) throws DataAccessException {
        def project = projectDataService.get(id)
        if (!project) {
            throw new DataAccessException("Not found: project with ID: ${id}")
        }
        project.description = data.getDescription()
        project.state = data.getState()
        try {
            projectDataService.save(project)
        } catch (Exception e) {
            throw new DataAccessException("Error: could not update project ${id}: ${e}", e)
        }
    }

    @Override
    void delete(final String projectName) throws DataAccessException {
        def project = projectDataService.getByName(projectName)
        if (!project) {
            throw new DataAccessException("Project does not exist: ${projectName}")
        }
        try {
            projectDataService.delete(project.getId())
        } catch (Exception e) {
            throw new DataAccessException("Project does not exist: ${projectName} : ${e}", e)
        }
    }

    @Override
    RdProject findByName (final String name) {
        RdProject project = projectDataService.getByName(name)
        return project ?: null
    }

    @Override
    RdProject findByNameAndState(String name, RdProject.State state) {
        RdProject project = projectDataService.getByName(name)
        if (!project) return null

        def pstate = project.state ?: RdProject.State.ENABLED
        def qstate = state ?: RdProject.State.ENABLED

        return (pstate == qstate) ? project : null
    }

    @Override
    boolean projectExists(String project) {
        projectDataService.countByName(project) > 0
    }

    @Override
    Collection<String> getFrameworkProjectNames() {
        return projectDataService.findProjectName()
    }

    @Override
    Collection<String> getFrameworkProjectNamesByState(RdProject.State state) {
        def namelist = []
        // If searching for enabled, we must include legacy projects with null in their field.
        if(state == RdProject.State.ENABLED) {
            namelist.addAll(projectDataService.findProjectNameByState(null))
        }
        namelist.addAll(projectDataService.findProjectNameByState(state))
        return namelist
    }

    @Override
    int countFrameworkProjects() {
        return projectDataService.count()
    }

    @Override
    int countFrameworkProjectsByState(RdProject.State state) {
        int count = 0
        // If searching for enabled, we must include legacy projects with null in their field.
        if(state == RdProject.State.ENABLED) {
            count += projectDataService.countProjectByState(null)
        }
        count += projectDataService.countProjectByState(state)
        return count
    }

    @Override
    String getProjectDescription(String name){
        return projectDataService.findProjectDescription(name)
    }

}
