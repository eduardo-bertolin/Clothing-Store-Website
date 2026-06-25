package com.fhcs.clothing_store.infrastructure.in.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fhcs.clothing_store.application.port.in.service.UserServicePort;
import com.fhcs.clothing_store.core.domain.bo.UserBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.UserPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.UserResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.mapper.UserDtoMapper;
import com.fhcs.clothing_store.util.JsonPatchUtil;
import com.github.fge.jsonpatch.JsonPatch;

@RestController
@RequestMapping("/api/users")
public class UserControllerAdapter {

    private final UserServicePort userService;
    private final JsonPatchUtil jsonPatchUtil;
    private final UserDtoMapper userMapper;

    public UserControllerAdapter(UserServicePort userService, JsonPatchUtil jsonPatchUtil,
            UserDtoMapper userMapper) {
        this.userService = userService;
        this.jsonPatchUtil = jsonPatchUtil;
        this.userMapper = userMapper;
    }

    @GetMapping("/info")
    public ResponseEntity<UserResponse> getUserInformation(
            @RequestHeader("Authorization") String token) {
        try {
            String accessToken = token.substring(7);
            UserBO user = userService.getUserByToken(accessToken);
            return ResponseEntity.ok(userMapper.toUserResponse(user,
                    "Informações do usuário recuperadas com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(userMapper.toUserError(
                            "Erro ao recuperar informações do usuário: " + e.getMessage()));
        }
    }

    @PatchMapping("/info")
    public ResponseEntity<UserResponse> updateUserInformation(
            @RequestHeader("Authorization") String token, @RequestBody JsonPatch patch) {
        try {
            String accessToken = token.substring(7);
            UserPatchDto patchDto = jsonPatchUtil.extractPatchedFields(patch, UserPatchDto.class);
            UserBO user = userService.updateUser(accessToken, patchDto);
            return ResponseEntity.ok(userMapper.toUserResponse(user,
                    "Informações do usuário atualizadas com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(userMapper.toUserError(
                            "Erro ao atualizar informações do usuário: " + e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<UserResponse> deleteUser(@RequestHeader("Authorization") String token) {
        try {
            String accessToken = token.substring(7);
            userService.deleteUser(accessToken);
            return ResponseEntity.ok(UserResponse.messageOnly("Usuário deletado com sucesso!", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(userMapper.toUserError("Erro ao deletar usuário: " + e.getMessage()));
        }
    }
}
