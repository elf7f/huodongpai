package com.huodongpai.converter;

import com.huodongpai.common.enums.EnableStatusEnum;
import com.huodongpai.dto.user.UserAddDTO;
import com.huodongpai.dto.user.UserUpdateDTO;
import com.huodongpai.entity.SysUser;
import com.huodongpai.vo.user.UserPageVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

public final class UserConverter {

    private UserConverter() {
    }

    public static SysUser toEntity(UserAddDTO addDTO, PasswordEncoder passwordEncoder) {
        SysUser user = new SysUser();
        user.setUsername(addDTO.getUsername().trim());
        user.setPassword(passwordEncoder.encode(addDTO.getPassword()));
        user.setRealName(addDTO.getRealName().trim());
        user.setPhone(StringUtils.hasText(addDTO.getPhone()) ? addDTO.getPhone().trim() : null);
        user.setRole(addDTO.getRole());
        user.setStatus(addDTO.getStatus() == null ? EnableStatusEnum.ENABLED.getCode() : addDTO.getStatus());
        return user;
    }

    public static void applyUpdate(SysUser user, UserUpdateDTO updateDTO, PasswordEncoder passwordEncoder) {
        user.setUsername(updateDTO.getUsername().trim());
        if (StringUtils.hasText(updateDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        user.setRealName(updateDTO.getRealName().trim());
        user.setPhone(StringUtils.hasText(updateDTO.getPhone()) ? updateDTO.getPhone().trim() : null);
        user.setRole(updateDTO.getRole());
        user.setStatus(updateDTO.getStatus());
    }

    public static UserPageVO toPageVO(SysUser user) {
        UserPageVO userPageVO = new UserPageVO();
        userPageVO.setId(user.getId());
        userPageVO.setUsername(user.getUsername());
        userPageVO.setRealName(user.getRealName());
        userPageVO.setPhone(user.getPhone());
        userPageVO.setRole(user.getRole());
        userPageVO.setStatus(user.getStatus());
        userPageVO.setCreateTime(user.getCreateTime());
        userPageVO.setUpdateTime(user.getUpdateTime());
        return userPageVO;
    }
}
