package com.ruan.mapper;

import com.ruan.bean.Employee;
import com.ruan.bean.dto.EmployeeDto;
import com.ruan.dao.BaseDao;
import com.ruan.util.CRUDUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmployeeMapper extends BaseDao {
    private final Connection conn; // 成员变量用于保存数据库连接

    public EmployeeMapper() {
        this.conn = BaseDao.getConnection();
    }

    // 判断是否登录成功
    public Employee isLogin(String realname, String password) {
        //System.out.println("conn: " + conn);
        // 定义sql语句
        String sql = "SELECT * FROM employee WHERE realname = ? AND password = ?";
        try {
            // 获取PreparedStatement对象
            assert conn != null;
            PreparedStatement ps = conn.prepareStatement(sql);
            // 设置参数
            ps.setString(1, realname);
            ps.setString(2, password);
            // 执行查询
            ResultSet rs = ps.executeQuery();
            // 判断是否有数据
            if (rs.next()) {
                // 获取数据
                int id = rs.getInt("id");
                int deptment_id = rs.getInt("deptment_id");
                int regist_level_id = rs.getInt("regist_level_id");
                int scheduling_id = rs.getInt("scheduling_id");
                // 封装成对象
                // 返回对象
                return new Employee(id, realname, password, deptment_id, regist_level_id, scheduling_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //注册用户
    public boolean isRegister(String realname, String password, Integer deptment_id,Integer regist_level_id, Integer scheduling_id){
        try {
            // 检查用户名是否已存在
            if (isUsernameExists(realname, conn)) {
                System.out.println("用户名已存在");
                return false;
            }

            // 执行插入操作
            String sql = "INSERT INTO employee (realname, password, deptment_id,regist_level_id,scheduling_id )"+" VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, realname);
            stmt.setString(2, password);
            stmt.setInt(3, deptment_id);
            stmt.setInt(4, regist_level_id);
            stmt.setInt(5, scheduling_id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //查询所有用户
    public List<EmployeeDto> listAll(){
            //编写sql语句
            String sql = "SELECT * FROM employee "+
                    "left join department d on employee.deptment_id = d.id "+
                    "left join regist_level rl on employee.regist_level_id = rl.id"+
                    " left join scheduling s on employee.scheduling_id = s.id";
        //将最终所有的感思信总返回
            return (List<EmployeeDto>) CRUDUtil.CRUD(sql, EmployeeDto.class, null, true, true);
    }
    private boolean isUsernameExists(String realname, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM employee WHERE realname = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, realname);
            rs = stmt.executeQuery();
            return rs.next();
        } finally {
            closeResources(rs, stmt, null);
        }
    }
    private void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
