package com.rascal.module.sys.service.test;

import com.rascal.module.sys.service.MenuService;
import com.rascal.module.sys.vo.NavMenuVO;
import com.rascal.support.test.SpringTransactionalTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MenuServiceTest extends SpringTransactionalTestCase {

    @Autowired
    private MenuService menuService;

    @Test
    public void findAvailableNavMenuVOs() {
        List<NavMenuVO> navMenuVOs = menuService.findAvailableNavMenuVOs();
        logger.debug("navMenuVOs size={}", navMenuVOs.size());
        Assert.assertTrue(navMenuVOs.size() > 0);
    }
}
