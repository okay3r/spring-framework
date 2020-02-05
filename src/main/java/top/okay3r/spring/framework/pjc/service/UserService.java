package top.okay3r.spring.framework.pjc.service;

import top.okay3r.spring.framework.pjc.pojo.User;

import java.util.List;

/**
 * Created By okay3r.top
 * Author: okay3r
 * Date: 2020/2/5
 * Time: 00:48
 * Explain:
 */
public interface UserService {
    List<User> queryUserList(Object param);
}
