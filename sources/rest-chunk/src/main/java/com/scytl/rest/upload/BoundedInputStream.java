package com.scytl.rest.upload;

import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream extends InputStream {

    /** the wrapped input stream */
    private final InputStream _in;

    /** the max length to provide */
    private final long _max;

    /** the number of bytes already returned */
    private long _pos = 0;

    /** the marked position */
    private long _mark = -1;

    private final boolean _fail;
    
    /** flag if close shoud be propagated */
    private boolean _propagateClose = true;

    public BoundedInputStream(final InputStream in, long size, boolean fail) {
        this._in = in;
        this._max = size;
        this._fail = fail;
    }
    
    /**
     * Creates a new <code>BoundedInputStream</code> that wraps the given input
     * stream and limits it to a certain size.
     *
     * @param in The wrapped input stream
     * @param size The maximum number of bytes to return
     */
    public BoundedInputStream(InputStream in, long size) {
        this(in, size, false);
    }

    /**
     * Creates a new <code>BoundedInputStream</code> that wraps the given input
     * stream and is unlimited.
     *
     * @param in The wrapped input stream
     */
    public BoundedInputStream(InputStream in) {
        this(in, -1);
    }

    /**
     * Invokes the delegate's <code>read()</code> method if
     * the current position is less than the limit.
     * @return the byte read or -1 if the end of stream or
     * the limit has been reached.
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read() throws IOException {
        if (_max >= 0 && _pos >= _max) {
            if(_fail) {
                throw new IllegalArgumentException("More than expected bytes are trying to be read.");
            } else {
                return -1;
            }
        }
        int result = _in.read();
        _pos++;
        return result;
    }

    /**
     * Invokes the delegate's <code>read(byte[])</code> method.
     * @param b the buffer to read the bytes into
     * @return the number of bytes read or -1 if the end of stream or
     * the limit has been reached.
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    /**
     * Invokes the delegate's <code>read(byte[], int, int)</code> method.
     * @param b the buffer to read the bytes into
     * @param off The start offset
     * @param len The number of bytes to read
     * @return the number of bytes read or -1 if the end of stream or
     * the limit has been reached.
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (_max>=0 && (_pos>=_max || _pos+len >= _max)) {
            if(_fail) {
                throw new IllegalArgumentException("More than expected bytes are trying to be read.");
            } else {
                return -1;
            }
        }
        long maxRead = _max>=0 ? Math.min(len, _max-_pos) : len;
        int bytesRead = _in.read(b, off, (int)maxRead);

        if (bytesRead==-1) {
            return -1;
        }

        _pos+=bytesRead;
        return bytesRead;
    }

    /**
     * Invokes the delegate's <code>skip(long)</code> method.
     * @param n the number of bytes to skip
     * @return the actual number of bytes skipped
     * @throws IOException if an I/O error occurs
     */
    @Override
    public long skip(long n) throws IOException {
        long toSkip = _max>=0 ? Math.min(n, _max-_pos) : n;
        long skippedBytes = _in.skip(toSkip);
        _pos+=skippedBytes;
        return skippedBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() throws IOException {
        if (_max>=0 && _pos>=_max) {
            return 0;
        }
        return _in.available();
    }

    /**
     * Invokes the delegate's <code>toString()</code> method.
     * @return the delegate's <code>toString()</code>
     */
    @Override
    public String toString() {
        return _in.toString();
    }

    /**
     * Invokes the delegate's <code>close()</code> method
     * if {@link #isPropagateClose()} is {@code true}.
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (_propagateClose) {
            _in.close();
        }
    }

    /**
     * Invokes the delegate's <code>reset()</code> method.
     * @throws IOException if an I/O error occurs
     */
    @Override
    public synchronized void reset() throws IOException {
        _in.reset();
        _pos = _mark;
    }

    /**
     * Invokes the delegate's <code>mark(int)</code> method.
     * @param readlimit read ahead limit
     */
    @Override
    public synchronized void mark(int readlimit) {
        _in.mark(readlimit);
        _mark = _pos;
    }

    /**
     * Invokes the delegate's <code>markSupported()</code> method.
     * @return true if mark is supported, otherwise false
     */
    @Override
    public boolean markSupported() {
        return _in.markSupported();
    }

}
