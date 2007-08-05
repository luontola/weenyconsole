/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jdave.runner;

import jdave.Specification;

import java.lang.reflect.Method;

/**
 * @author Pekka Enberg
 * @author Joni Freeman
 */
public class SpecRunner {
    public <T extends Specification<?>> void visit(Class<T> specType, ISpecVisitor callback) {
        for (Class<?> contextType : reverse(specType.getDeclaredClasses())) {
            Context context = new Context(specType, contextType) {
                @Override
                protected Behavior newBehavior(Method method, Class<? extends Specification<?>> specType, Class<?> contextType) {
                    return new VisitingBehavior(method);
                }
            };
            run(callback, context);
        }
    }

    public <T extends Specification<?>> void run(Class<T> specType, ISpecVisitor callback) {
        for (Class<?> contextType : reverse(specType.getDeclaredClasses())) {
            Context context = new Context(specType, contextType) {
                @Override
                protected Behavior newBehavior(Method method, Class<? extends Specification<?>> specType, Class<?> contextType) {
                    return new ExecutingBehavior(method, specType, contextType);
                }
            };
            run(callback, context);
        }
    }

    private static Class<?>[] reverse(Class<?>[] classes) {
        for (int i = 0; i < classes.length / 2; i++) {
            Class<?> tmp = classes[i];
            classes[i] = classes[(classes.length - 1) - i];
            classes[(classes.length - 1) - i] = tmp;
        }
        return classes;
    }

    private void run(ISpecVisitor callback, Context context) {
        if (context.isContextClass()) {
            callback.onContext(context);
            context.run(callback);
            callback.afterContext(context);
        }
    }
}