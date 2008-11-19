/*
 * This file is part of WeenyConsole <http://www.orfjackal.net/>
 *
 * Copyright (c) 2007-2008, Esko Luontola. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *
 *     * Neither the name of the copyright holder nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.orfjackal.weenyconsole.converters;

import jdave.Block;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import net.orfjackal.weenyconsole.ConversionService;
import net.orfjackal.weenyconsole.exceptions.InvalidSourceValueException;
import net.orfjackal.weenyconsole.exceptions.TargetTypeNotSupportedException;
import org.jmock.Expectations;
import org.junit.runner.RunWith;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
@RunWith(JDaveRunner.class)
public class DelegatingConverterSpec extends Specification<DelegatingConverter> {

    public class ADelegatingConverter {

        private ConversionService provider;
        private DelegatingConverter delegator;

        public DelegatingConverter create() {
            provider = mock(ConversionService.class, "provider");
            delegator = new DelegatingConverter(int.class, Integer.class);
            delegator.setProvider(provider);
            return null;
        }

        public void shouldDelegateConversionsOfOneTypeToTheConverterOfAnotherType() throws TargetTypeNotSupportedException, InvalidSourceValueException {
            checking(new Expectations(){{
                one(provider).valueOf("1", Integer.class); will(returnValue(1));
            }});
            specify(delegator.supportedTargetType(), should.equal(int.class));
            specify(delegator.valueOf("1", int.class), should.equal(1));
        }

        public void shouldNotAllowAnInfiniteLoopWhenSourceAndTargetTypesAreTheSame() {
            specify(new Block() {
                public void run() throws Throwable {
                    new DelegatingConverter(Integer.class, Integer.class);
                }
            }, should.raise(IllegalArgumentException.class));
        }

        public void shouldNotAllowAnInfiniteLoopWhenSourceIsASubclassOfTheTargetType() {
            specify(new Block() {
                public void run() throws Throwable {
                    new DelegatingConverter(Integer.class, Number.class);
                }
            }, should.raise(IllegalArgumentException.class, "java.lang.Integer is a subclass of java.lang.Number"));
        }

        public void shouldNotAllowAnInfiniteLoopWhenSourceIsASuperclassOfTheTargetType() {
            specify(new Block() {
                public void run() throws Throwable {
                    new DelegatingConverter(Number.class, Integer.class);
                }
            }, should.raise(IllegalArgumentException.class, "java.lang.Integer is a subclass of java.lang.Number"));
        }
    }
}
