/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.web.method.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 *
 * @author Rob Winch
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CsrfTokenArgumentResolverTests {
	@Mock
	private ModelAndViewContainer mavContainer;
	@Mock
	private WebDataBinderFactory binderFactory;

	private MockHttpServletRequest request;
	private NativeWebRequest webRequest;

	private CsrfToken token;

	private CsrfTokenArgumentResolver resolver;

	@Before
	public void setup() {
		token = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "secret");
		resolver = new CsrfTokenArgumentResolver();
		request = new MockHttpServletRequest();
		webRequest = new ServletWebRequest(request);
	}

	@Test
	public void supportsParameterFalse() {
		assertThat(resolver.supportsParameter(noToken())).isFalse();
	}

	@Test
	public void supportsParameterTrue() {
		assertThat(resolver.supportsParameter(token())).isTrue();
	}

	@Test
	public void resolveArgumentNotFound() throws Exception {
		assertThat(
				resolver.resolveArgument(token(), mavContainer, webRequest, binderFactory))
				.isNull();
	}

	@Test
	public void resolveArgumentFound() throws Exception {
		request.setAttribute(CsrfToken.class.getName(), token);

		assertThat(
				resolver.resolveArgument(token(), mavContainer, webRequest, binderFactory))
				.isSameAs(token);
	}

	private MethodParameter noToken() {
		return getMethodParameter("noToken", String.class);
	}

	private MethodParameter token() {
		return getMethodParameter("token", CsrfToken.class);
	}

	private MethodParameter getMethodParameter(String methodName, Class<?>... paramTypes) {
		Method method = ReflectionUtils.findMethod(TestController.class, methodName,
				paramTypes);
		return new MethodParameter(method, 0);
	}

	public static class TestController {
		public void noToken(String user) {
		}

		public void token(CsrfToken token) {
		}
	}

}
