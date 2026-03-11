package com.huodongpai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huodongpai.common.enums.EnableStatusEnum;
import com.huodongpai.common.enums.UserRoleEnum;
import com.huodongpai.common.result.PageResponse;
import com.huodongpai.dto.user.UserAddDTO;
import com.huodongpai.dto.user.UserPageQueryDTO;
import com.huodongpai.dto.user.UserStatusUpdateDTO;
import com.huodongpai.dto.user.UserUpdateDTO;
import com.huodongpai.entity.SysUser;
import com.huodongpai.exception.BusinessException;
import com.huodongpai.mapper.SysUserMapper;
import com.huodongpai.service.UserService;
import com.huodongpai.vo.user.UserPageVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageResponse<UserPageVO> getPage(UserPageQueryDTO queryDTO) {
        IPage<SysUser> page = sysUserMapper.selectPage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()),
                Wrappers.<SysUser>lambdaQuery()
                        .and(StringUtils.hasText(queryDTO.getKeyword()), wrapper -> wrapper
                                .like(SysUser::getUsername, queryDTO.getKeyword())
                                .or()
                                .like(SysUser::getRealName, queryDTO.getKeyword())
                                .or()
                                .like(SysUser::getPhone, queryDTO.getKeyword()))
                        .eq(StringUtils.hasText(queryDTO.getRole()), SysUser::getRole, queryDTO.getRole())
                        .eq(queryDTO.getStatus() != null, SysUser::getStatus, queryDTO.getStatus())
                        .orderByDesc(SysUser::getCreateTime)
                        .orderByDesc(SysUser::getId));
        return PageResponse.of(page.convert(this::toVO));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(UserAddDTO addDTO, Long operatorId) {
        validateRole(addDTO.getRole());
        checkUnique(addDTO.getUsername(), addDTO.getPhone(), null);
        SysUser user = new SysUser();
        user.setUsername(addDTO.getUsername().trim());
        user.setPassword(passwordEncoder.encode(addDTO.getPassword()));
        user.setRealName(addDTO.getRealName().trim());
        user.setPhone(addDTO.getPhone());
        user.setRole(addDTO.getRole());
        user.setStatus(addDTO.getStatus() == null ? EnableStatusEnum.ENABLED.getCode() : addDTO.getStatus());
        sysUserMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateDTO updateDTO, Long operatorId) {
        validateRole(updateDTO.getRole());
        SysUser user = requireUser(updateDTO.getId());
        validateAdminRetention(user, updateDTO.getRole(), updateDTO.getStatus(), operatorId);
        checkUnique(updateDTO.getUsername(), updateDTO.getPhone(), user.getId());
        user.setUsername(updateDTO.getUsername().trim());
        if (StringUtils.hasText(updateDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        user.setRealName(updateDTO.getRealName().trim());
        user.setPhone(updateDTO.getPhone());
        user.setRole(updateDTO.getRole());
        user.setStatus(updateDTO.getStatus());
        if (sysUserMapper.updateById(user) != 1) {
            throw new BusinessException("用户更新失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, UserStatusUpdateDTO updateDTO, Long operatorId) {
        SysUser user = requireUser(id);
        validateAdminRetention(user, user.getRole(), updateDTO.getStatus(), operatorId);
        user.setStatus(updateDTO.getStatus());
        if (sysUserMapper.updateById(user) != 1) {
            throw new BusinessException("用户状态更新失败，请稍后重试");
        }
    }

    private void validateRole(String role) {
        UserRoleEnum.fromCode(role);
    }

    private void checkUnique(String username, String phone, Long excludeId) {
        long usernameCount = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, username.trim())
                .ne(excludeId != null, SysUser::getId, excludeId));
        if (usernameCount > 0) {
            throw new BusinessException("用户名已存在");
        }
        if (StringUtils.hasText(phone)) {
            long phoneCount = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                    .eq(SysUser::getPhone, phone)
                    .ne(excludeId != null, SysUser::getId, excludeId));
            if (phoneCount > 0) {
                throw new BusinessException("手机号已存在");
            }
        }
    }

    private SysUser requireUser(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private void validateAdminRetention(SysUser existingUser, String newRole, Integer newStatus, Long operatorId) {
        boolean wasEnabledAdmin = UserRoleEnum.ADMIN.getCode().equals(existingUser.getRole())
                && EnableStatusEnum.ENABLED.getCode().equals(existingUser.getStatus());
        boolean willRemainEnabledAdmin = UserRoleEnum.ADMIN.getCode().equals(newRole)
                && EnableStatusEnum.ENABLED.getCode().equals(newStatus);
        if (!wasEnabledAdmin || willRemainEnabledAdmin) {
            return;
        }
        if (existingUser.getId().equals(operatorId)) {
            throw new BusinessException("当前登录管理员不能取消自己的管理员权限或禁用自己");
        }
        long enabledAdminCount = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getRole, UserRoleEnum.ADMIN.getCode())
                .eq(SysUser::getStatus, EnableStatusEnum.ENABLED.getCode()));
        if (enabledAdminCount <= 1) {
            throw new BusinessException("系统至少需要保留一个启用状态的管理员");
        }
    }

    private UserPageVO toVO(SysUser user) {
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
