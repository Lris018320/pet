package com.pet.platform.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.platform.entity.Admin;
import com.pet.platform.entity.Merchant;
import com.pet.platform.mapper.AdminMapper;
import com.pet.platform.mapper.MerchantMapper;
import com.pet.platform.util.BcryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 首次启动时若 admin/merchant 表为空，自动写入默认账号
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public void run(String... args) {
        Long adminCount = adminMapper.selectCount(new LambdaQueryWrapper<>());
        if (adminCount == 0) {
            Admin admin = new Admin();
            admin.setAccount("admin");
            admin.setPassword(BcryptUtil.encode("123456"));
            adminMapper.insert(admin);
            log.info("====================================================");
            log.info("已初始化管理员账号: admin / 123456  请及时修改密码！");
            log.info("====================================================");
        }

        Long merchantCount = merchantMapper.selectCount(new LambdaQueryWrapper<>());
        if (merchantCount == 0) {
            Merchant merchant = new Merchant();
            merchant.setAccount("merchant");
            merchant.setPassword(BcryptUtil.encode("123456"));
            merchant.setName("官方旗舰店");
            merchant.setPhone("13800138000");
            merchantMapper.insert(merchant);
            log.info("====================================================");
            log.info("已初始化商家账号: merchant / 123456  请及时修改密码！");
            log.info("====================================================");
        }
    }
}
