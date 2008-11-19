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
import net.orfjackal.weenyconsole.Converter;
import net.orfjackal.weenyconsole.exceptions.ConversionFailedException;
import org.junit.runner.RunWith;

import java.awt.*;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
@RunWith(JDaveRunner.class)
public class StringConstructorConverterSpec extends Specification<Converter> {

    public class AStringConstructorConverter {

        private StringConstructorConverter converter;

        public Converter create() {
            converter = new StringConstructorConverter();
            return converter;
        }

        public void shouldConvertUsingTheStringConstructorOfTheClass() throws ConversionFailedException {
            specify(converter.valueOf("1", Integer.class), should.equal(1));
        }

        public void shouldFailIfTheValueCanNotBeConverted() {
            specify(new Block() {
                public void run() throws Throwable {
                    converter.valueOf("not a number", Integer.class);
                }
            }, should.raise(ConversionFailedException.class));
        }

        public void shouldFailIfTheTargetClassHasNoStringConstructor() {
            specify(new Block() {
                public void run() throws Throwable {
                    converter.valueOf("1,2", Point.class);
                }
            }, should.raise(ConversionFailedException.class));
        }
    }
}
