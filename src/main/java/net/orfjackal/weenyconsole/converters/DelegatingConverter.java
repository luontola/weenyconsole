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

import net.orfjackal.weenyconsole.ConversionService;
import net.orfjackal.weenyconsole.Converter;
import net.orfjackal.weenyconsole.exceptions.InvalidSourceValueException;
import net.orfjackal.weenyconsole.exceptions.TargetTypeNotSupportedException;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class DelegatingConverter implements Converter {

    private Class<?> delegateFrom;
    private Class<?> delegateTo;
    private ConversionService provider;

    public DelegatingConverter(Class<?> delegateFrom, Class<?> delegateTo) {
        if (delegateFrom.isAssignableFrom(delegateTo)) {
            throw new IllegalArgumentException(delegateTo.getName() + " is a subclass of " + delegateFrom.getName());
        }
        if (delegateTo.isAssignableFrom(delegateFrom)) {
            throw new IllegalArgumentException(delegateFrom.getName() + " is a subclass of " + delegateTo.getName());
        }
        this.delegateFrom = delegateFrom;
        this.delegateTo = delegateTo;
    }

    @SuppressWarnings({"unchecked"})
    public Object valueOf(String sourceValue, Class<?> targetType) throws TargetTypeNotSupportedException, InvalidSourceValueException {
        return provider.valueOf(sourceValue, delegateTo);
    }

    public Class<?> supportedTargetType() {
        return delegateFrom;
    }

    public void setProvider(ConversionService provider) {
        this.provider = provider;
    }
}
