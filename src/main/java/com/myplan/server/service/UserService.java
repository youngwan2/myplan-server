package com.myplan.server.service;

import com.myplan.server.dto.UserDTO;
import com.myplan.server.dto.UserInfoDTO;
import com.myplan.server.exception.AlreadyExistsException;
import com.myplan.server.exception.NotFound;
import com.myplan.server.exception.UserNotFoundException;
import com.myplan.server.model.Member;
import com.myplan.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshService refreshService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원유무
    public boolean existsUser(Long id){
       userRepository.findById(id).orElseThrow(()-> new NotFound("User wasn't found"));
       return true;
    }

    // 회원정보 조회(전체- 외래키 관계 설정용)
    public Member getUsersById(Long id){
        return userRepository.findById(id).orElseThrow(()->new UserNotFoundException("User Id is Not Found: "+ id));
    }

    // 회원정보 조회(일부- 사용자 응답)
    public UserInfoDTO getUsersByUsername(String username) {
        Member user = userRepository.findByUsername(username);

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(user.getId());
        userInfoDTO.setEmail(user.getEmail());
        userInfoDTO.setUsername(username);
        userInfoDTO.setCreatedAt(user.getCreatedAt());
        userInfoDTO.setRole(user.getRole());

        return userInfoDTO;

    }

    // 회원가입
    public Member registerUser(UserDTO userDto) {

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new AlreadyExistsException("이미 존재하는 유저 이름 입니다.");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new AlreadyExistsException("이미 존재하는 유저 이메일 입니다.");
        }

        Member user = Member.builder()
                .username(userDto.getUsername())
                .password(bCryptPasswordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .role("ROLE_USER").build();

        userRepository.save(user);

        return user;
    }

    // 회원탈퇴
    public void deleteUser(Long id) throws IllegalAccessException {
        if (id == null) throw new IllegalAccessException("id 가 null 입니다.");
        Member user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found with ID:" + id));
        String username = user.getUsername();

        log.info("username={}, id={}", username, id);

        refreshService.removeRefresh(username);
        userRepository.deleteById(id);
    }

    // 회원정보 수정
    public boolean updateUser(Long id, String password) {
        if (id == null || password == null) {
            throw new IllegalArgumentException("id 혹은 password 가 null 입니다.");
        }
        log.info("id:{},password:{}, encode:{}", id, password, bCryptPasswordEncoder.encode(password));

        Member user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id"));
        log.info(user.toString());
        user.setPassword(bCryptPasswordEncoder.encode(password));

        return true;
    }
}
