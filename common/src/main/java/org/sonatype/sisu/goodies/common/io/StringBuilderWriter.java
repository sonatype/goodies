/*
 * Copyright (c) 2007-2011 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.sisu.goodies.common.io;

import java.io.Writer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Writer} buffer using {@link StringBuilder}.
 *
 * @since 1.0
 */
public class StringBuilderWriter
    extends Writer
{
    private static final String NULL = "null"; //NON-NLS

    private final StringBuilder buffer;

    public StringBuilderWriter() {
        this(new StringBuilder());
    }

    private StringBuilderWriter(final StringBuilder buffer) {
        this.buffer = checkNotNull(buffer);
        this.lock = buffer;
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    @Override
    public void write(final int c) {
        buffer.append((char) c);
    }

    @Override
    public void write(final char cbuf[], final int off, final int len) {
        if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
        else if (len == 0) {
            return;
        }
        buffer.append(cbuf, off, len);
    }

    @Override
    public void write(final String str) {
        buffer.append(str);
    }

    @Override
    public void write(final String str, final int off, final int len) {
        buffer.append(str.substring(off, off + len));
    }

    @Override
    public StringBuilderWriter append(final CharSequence csq) {
        if (csq == null) {
            write(NULL);
        }
        else {
            write(csq.toString());
        }
        return this;
    }

    @Override
    public StringBuilderWriter append(final CharSequence csq, final int start, final int end) {
        CharSequence cs = (csq == null ? NULL : csq);
        write(cs.subSequence(start, end).toString());
        return this;
    }

    @Override
    public StringBuilderWriter append(final char c) {
        write(c);
        return this;
    }

    @Override
    public String toString() {
        return buffer.toString();
    }

    @Override
    public void flush() {
        // nop
    }

    @Override
    public void close() {
        // nop
    }
}
