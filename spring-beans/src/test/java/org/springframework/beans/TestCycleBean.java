package org.springframework.beans;

import org.junit.Test;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.tests.sample.beans.DummyBean;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @Author zl
 * @Date 2019-12-16
 * @Des ${todo}
 */
public class TestCycleBean {

	@Test
	public void testResourceInjection() {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
		bd.setScope(RootBeanDefinition.SCOPE_SINGLETON);
		bf.registerBeanDefinition("test", bd);
        TestBean testBean = (TestBean) bf.getBean("test");

		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(DummyBean.class);
		rootBeanDefinition.setScope(RootBeanDefinition.SCOPE_SINGLETON);
		bf.registerBeanDefinition("dummy", rootBeanDefinition);

		DummyBean dummyBean = (DummyBean)bf.getBean("dummy");
	}

	@Test
	public void testDependsOnCycle() {
		DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
		RootBeanDefinition bd1 = new RootBeanDefinition(TestBean.class);
		bd1.setDependsOn("tb2");
		lbf.registerBeanDefinition("tb1", bd1);
		RootBeanDefinition bd2 = new RootBeanDefinition(TestBean.class);
		bd2.setDependsOn("tb1");
		lbf.registerBeanDefinition("tb2", bd2);
		try {
			lbf.preInstantiateSingletons();
			fail("Should have thrown BeanCreationException");
		}
		catch (BeanCreationException ex) {
			// expected
			assertTrue(ex.getMessage().contains("Circular"));
			assertTrue(ex.getMessage().contains("'tb2'"));
			assertTrue(ex.getMessage().contains("'tb1'"));
		}
	}

}
