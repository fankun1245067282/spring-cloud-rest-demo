package com.fankun.jdbc.repository;

import com.fankun.jdbc.domain.MyUser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Collection;

/**
 * 用户的仓储(sql,nosql或者内存型)
 */
@Repository
public class UserRepository {

    private final DataSource dataSource;

    private final DataSource masterDataSource;
    private final DataSource slaveDataSource;
    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager platformTransactionManager;
    public UserRepository(DataSource dataSource,
                          @Qualifier("masterDataSource") DataSource masterDataSource,
                          @Qualifier("slaveDataSource") DataSource slaveDataSource,
                          JdbcTemplate jdbcTemplate,
                          PlatformTransactionManager platformTransactionManager) {
        this.dataSource = dataSource;
        this.masterDataSource = masterDataSource;
        this.slaveDataSource = slaveDataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.platformTransactionManager = platformTransactionManager;
    }


    public boolean jdbcSave(MyUser user){
        System.out.printf("[Thread: %s] UserRepository saves user:%s\n",Thread.currentThread().getName(),user);
        boolean success = false;
        //默认自动提交方式
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);//设置非自动提交

            String sql = "insert into myuser(name) values (?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            Savepoint savepoint1 = connection.setSavepoint("t1");
//            connection.rollback(savepoint1);
//            connection.commit();
//            connection.releaseSavepoint(savepoint1);
            //提交之后，要释放保存点。。。。。。

            ps.setString(1,user.getName());
            int updateNum = ps.executeUpdate();
            if(updateNum>0){
                success = true;
            }
            ps.close();
            connection.commit();//提交
        } catch (SQLException e) {
            try {
                connection.rollback();//回滚
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return success;
    }

    @Transactional
    public boolean transactionSave(MyUser user){
        System.out.printf("[Thread: %s] UserRepository saves user:%s\n",Thread.currentThread().getName(),user);
        boolean success = false;
        success = jdbcTemplate.execute("insert into myuser(name) values (?)",
                new PreparedStatementCallback<Boolean>() {
                    @Override
                    public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                        ps.setString(1, user.getName());
                        int updateNum = ps.executeUpdate();
                        if (updateNum > 0) {
                            return true;
                        }//不加事务，差不多在这就提交了，加了等方法执行完才能提交
                        return false;
                    }
                });
        return success;
    }

    public boolean save(MyUser user){
        System.out.printf("[Thread: %s] UserRepository saves user:%s\n",Thread.currentThread().getName(),user);
        boolean success = false;
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(defaultTransactionDefinition);

        success = jdbcTemplate.execute("insert into myuser(name) values (?)",
                new PreparedStatementCallback<Boolean>() {
                    @Override
                    public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                        ps.setString(1, user.getName());
                        int updateNum = ps.executeUpdate();
                        if (updateNum > 0) {
                            return true;
                        }//不加事务，差不多在这就提交了，加了等方法执行完才能提交
                        return false;
                    }
                });
        //添加异常处理
        platformTransactionManager.commit(transactionStatus);
        return success;
    }
    public Collection<MyUser> findall(){
        return null;
    }
}
