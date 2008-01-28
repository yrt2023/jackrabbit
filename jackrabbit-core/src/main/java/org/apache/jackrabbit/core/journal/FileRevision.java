/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.core.journal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Maintains a file-based revision counter with locking, assuring uniqueness.
 */
public class FileRevision {

    /**
     * Underlying random access file.
     */
    private final RandomAccessFile raf;

    /**
     * Cached value.
     */
    private long value;

    /**
     * Creates a new file based revision counter.
     *
     * @param file holding global counter
     * @throws JournalException if some error occurs
     */
    public FileRevision(File file) throws JournalException {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            raf = new RandomAccessFile(file, "rw");
            if (raf.length() == 0) {
                set(0);
            }
        } catch (IOException e) {
            throw new JournalException(
                    "I/O error while attempting to create new file '" + file + "'.", e);
        }
    }

    /**
     * Return current counter value.
     *
     * @return counter value
     * @throws JournalException if some error occurs
     */
    public synchronized long get() throws JournalException {
        try {
            raf.seek(0L);
            value = raf.readLong();
            return value;
        } catch (IOException e) {
            throw new JournalException("I/O error occurred.", e);
        }
    }

    /**
     * Set current counter value.
     *
     * @param value new counter value
     * @throws JournalException if some error occurs
     */
    public synchronized void set(long value) throws JournalException {
        try {
            raf.seek(0L);
            raf.writeLong(value);
            raf.getFD().sync();
            this.value = value;
        } catch (IOException e) {
            throw new JournalException("I/O error occurred.", e);
        }
    }

}
