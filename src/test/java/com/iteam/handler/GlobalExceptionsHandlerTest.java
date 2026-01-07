package com.iteam.handler;

import com.iteam.Exceptions.NotFoundEntityExceptions;
import com.iteam.Exceptions.UserAlreadyExistsExceptions;
import com.iteam.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionsHandlerTest {

    @InjectMocks
    private GlobalExceptionsHandler globalExceptionsHandler;

    @Test
    void handleNotFoundEntityExceptions_ShouldReturnNotFoundResponse() {
        // Arrange
        NotFoundEntityExceptions exception = new NotFoundEntityExceptions("Ressource non trouvée");

        // Act
        ResponseEntity<ErrorResponse> response =
                globalExceptionsHandler.handleNotFoundEntityExceptions(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Ressources_Not_Found");
        assertThat(response.getBody().getMessage()).isEqualTo("Ressource non trouvée");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleUserAlreadyExistsExceptions_ShouldReturnConflictResponse() {
        // Arrange
        UserAlreadyExistsExceptions exception = new UserAlreadyExistsExceptions("L'utilisateur existe déjà");

        // Act
        ResponseEntity<ErrorResponse> response =
                globalExceptionsHandler.handleUserAlreadyExistsExceptions(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getError()).isEqualTo("User_Already_Exists");
        assertThat(response.getBody().getMessage()).isEqualTo("L'utilisateur existe déjà");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleAllExceptions_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        Exception exception = new Exception("Une erreur inattendue est survenue");

        // Act
        ResponseEntity<ErrorResponse> response =
                globalExceptionsHandler.handleAllExceptions(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal_Server_Error");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred. Please try again later.");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}