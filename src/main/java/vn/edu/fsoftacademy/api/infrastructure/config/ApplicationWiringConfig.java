package vn.edu.fsoftacademy.api.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.edu.fsoftacademy.api.application.command.changeemail.ChangeEmailCommandHandler;
import vn.edu.fsoftacademy.api.application.command.changepassword.ChangePasswordCommandHandler;
import vn.edu.fsoftacademy.api.application.command.createproject.CreateProjectCommandHandler;
import vn.edu.fsoftacademy.api.application.command.deleteaccount.DeleteAccountCommandHandler;
import vn.edu.fsoftacademy.api.application.command.deletedocument.DeleteDocumentCommandHandler;
import vn.edu.fsoftacademy.api.application.command.deleteproject.DeleteProjectCommandHandler;
import vn.edu.fsoftacademy.api.application.command.login.LoginCommandHandler;
import vn.edu.fsoftacademy.api.application.command.logout.LogoutCommandHandler;
import vn.edu.fsoftacademy.api.application.command.refreshsession.RefreshSessionCommandHandler;
import vn.edu.fsoftacademy.api.application.command.register.RegisterCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updatedocument.UpdateDocumentCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updateprofile.UpdateProfileCommandHandler;
import vn.edu.fsoftacademy.api.application.command.updateproject.UpdateProjectCommandHandler;
import vn.edu.fsoftacademy.api.application.command.uploaddocument.UploadDocumentCommandHandler;
import vn.edu.fsoftacademy.api.application.mapper.outbox.DocumentUploadedOutboxEventMapper;
import vn.edu.fsoftacademy.api.application.port.AccessTokenPort;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;
import vn.edu.fsoftacademy.api.application.port.PasswordHasher;
import vn.edu.fsoftacademy.api.application.port.RefreshTokenPort;
import vn.edu.fsoftacademy.api.application.port.SessionTokenPort;
import vn.edu.fsoftacademy.api.application.query.currentuser.GetCurrentUserQueryHandler;
import vn.edu.fsoftacademy.api.application.query.getdocument.GetDocumentQueryHandler;
import vn.edu.fsoftacademy.api.application.query.getproject.GetProjectQueryHandler;
import vn.edu.fsoftacademy.api.application.query.listdocuments.ListDocumentsQueryHandler;
import vn.edu.fsoftacademy.api.application.query.listprojects.ListProjectsQueryHandler;
import vn.edu.fsoftacademy.api.application.repository.OutboxEventRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectDocumentRepository;
import vn.edu.fsoftacademy.api.application.repository.ProjectRepository;
import vn.edu.fsoftacademy.api.application.repository.RefreshSessionRepository;
import vn.edu.fsoftacademy.api.application.repository.RoleRepository;
import vn.edu.fsoftacademy.api.application.repository.UserRepository;
import vn.edu.fsoftacademy.api.application.service.SessionTokenService;
import vn.edu.fsoftacademy.api.infrastructure.storage.StorageProperties;
import vn.edu.fsoftacademy.api.worker.OutboxWorkerProperties;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({ StorageProperties.class, OutboxWorkerProperties.class })
public class ApplicationWiringConfig {
  @Bean
  SessionTokenPort sessionTokenPort(
      AccessTokenPort accessTokens,
      RefreshTokenPort refreshTokens,
      RefreshSessionRepository refreshTokenRepository,
      UserRepository users) {
    return new SessionTokenService(accessTokens, refreshTokens, refreshTokenRepository, users);
  }

  @Bean
  RegisterCommandHandler registerCommandHandler(
      UserRepository users, RoleRepository roles, PasswordHasher passwords) {
    return new RegisterCommandHandler(users, roles, passwords);
  }

  @Bean
  LoginCommandHandler loginCommandHandler(
      UserRepository users, PasswordHasher passwords, SessionTokenPort sessions) {
    return new LoginCommandHandler(users, passwords, sessions);
  }

  @Bean
  LogoutCommandHandler logoutCommandHandler(SessionTokenPort sessions) {
    return new LogoutCommandHandler(sessions);
  }

  @Bean
  RefreshSessionCommandHandler refreshSessionCommandHandler(SessionTokenPort sessions) {
    return new RefreshSessionCommandHandler(sessions);
  }

  @Bean
  GetCurrentUserQueryHandler getCurrentUserQueryHandler(UserRepository users) {
    return new GetCurrentUserQueryHandler(users);
  }

  @Bean
  UpdateProfileCommandHandler updateProfileCommandHandler(UserRepository users) {
    return new UpdateProfileCommandHandler(users);
  }

  @Bean
  ChangeEmailCommandHandler changeEmailCommandHandler(
      UserRepository users, PasswordHasher passwords, SessionTokenPort sessions) {
    return new ChangeEmailCommandHandler(users, passwords, sessions);
  }

  @Bean
  ChangePasswordCommandHandler changePasswordCommandHandler(
      UserRepository users, PasswordHasher passwords, SessionTokenPort sessions) {
    return new ChangePasswordCommandHandler(users, passwords, sessions);
  }

  @Bean
  CreateProjectCommandHandler createProjectCommandHandler(ProjectRepository projects) {
    return new CreateProjectCommandHandler(projects);
  }

  @Bean
  ListProjectsQueryHandler listProjectsQueryHandler(ProjectRepository projects) {
    return new ListProjectsQueryHandler(projects);
  }

  @Bean
  GetProjectQueryHandler getProjectQueryHandler(ProjectRepository projects) {
    return new GetProjectQueryHandler(projects);
  }

  @Bean
  UpdateProjectCommandHandler updateProjectCommandHandler(ProjectRepository projects) {
    return new UpdateProjectCommandHandler(projects);
  }

  @Bean
  DeleteProjectCommandHandler deleteProjectCommandHandler(
      ProjectRepository projects, ProjectDocumentRepository documents, ObjectStorage storage) {
    return new DeleteProjectCommandHandler(projects, documents, storage);
  }

  @Bean
  UploadDocumentCommandHandler uploadDocumentCommandHandler(
      ProjectRepository projects,
      ProjectDocumentRepository documents,
      ObjectStorage storage,
      OutboxEventRepository outboxEvents,
      DocumentUploadedOutboxEventMapper outboxEventMapper,
      org.springframework.transaction.support.TransactionTemplate transactionTemplate) {
    return new UploadDocumentCommandHandler(
        projects, documents, storage, outboxEvents, outboxEventMapper, transactionTemplate);
  }

  @Bean
  ListDocumentsQueryHandler listDocumentsQueryHandler(
      ProjectRepository projects, ProjectDocumentRepository documents) {
    return new ListDocumentsQueryHandler(projects, documents);
  }

  @Bean
  GetDocumentQueryHandler getDocumentQueryHandler(
      ProjectRepository projects, ProjectDocumentRepository documents) {
    return new GetDocumentQueryHandler(projects, documents);
  }

  @Bean
  UpdateDocumentCommandHandler updateDocumentCommandHandler(
      ProjectRepository projects, ProjectDocumentRepository documents) {
    return new UpdateDocumentCommandHandler(projects, documents);
  }

  @Bean
  DeleteDocumentCommandHandler deleteDocumentCommandHandler(
      ProjectRepository projects, ProjectDocumentRepository documents, ObjectStorage storage) {
    return new DeleteDocumentCommandHandler(projects, documents, storage);
  }

  @Bean
  DeleteAccountCommandHandler deleteAccountCommandHandler(
      UserRepository users, PasswordHasher passwords, SessionTokenPort sessions) {
    return new DeleteAccountCommandHandler(users, passwords, sessions);
  }
}
