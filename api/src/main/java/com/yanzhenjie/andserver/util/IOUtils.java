/*
 * Copyright © 2018 Zhenjie Yan.
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
package com.yanzhenjie.andserver.util;

import android.os.Build;
import android.os.StatFs;
import android.text.TextUtils;

import com.yanzhenjie.andserver.http.RequestBody;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created in 2016/4/12 21:21.
 */
public class IOUtils {

    private static final byte[] EMPTY_CONTENT = new byte[0];

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }

        RequestBody requestBody;
    }

    public static void flushQuietly(Flushable flushable) {
        if (flushable != null) {
            try {
                flushable.flush();
            } catch (Exception ignored) {
            }
        }
    }

    public static BufferedInputStream toBufferedInputStream(InputStream inputStream) {
        return inputStream instanceof BufferedInputStream
            ? (BufferedInputStream) inputStream
            : new BufferedInputStream(inputStream);
    }

    public static BufferedOutputStream toBufferedOutputStream(OutputStream outputStream) {
        return outputStream instanceof BufferedOutputStream
            ? (BufferedOutputStream) outputStream
            : new BufferedOutputStream(outputStream);
    }

    public static BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public static BufferedWriter toBufferedWriter(Writer writer) {
        return writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer);
    }

    public static InputStream toInputStream(CharSequence input) {
        return new ByteArrayInputStream(input.toString().getBytes());
    }

    public static InputStream toInputStream(CharSequence input, String charset) {
        return toInputStream(input, Charset.forName(charset));
    }

    public static InputStream toInputStream(CharSequence input, Charset charset) {
        byte[] bytes = input.toString().getBytes(charset);
        return new ByteArrayInputStream(bytes);
    }

    public static InputStream createEmptyInput() {
        return new ByteArrayInputStream(EMPTY_CONTENT);
    }

    public static InputStream toNonClosing(InputStream in) {
        Assert.notNull(in, "No InputStream specified");
        return new NonClosingInputStream(in);
    }

    public static OutputStream toNonClosing(OutputStream out) {
        Assert.notNull(out, "No OutputStream specified.");
        return new NonClosingOutputStream(out);
    }

    public static String toString(InputStream input) throws IOException {
        return new String(toByteArray(input));
    }

    public static String toString(InputStream input, String charset) throws IOException {
        return new String(toByteArray(input), charset);
    }

    public static String toString(InputStream input, Charset charset) throws IOException {
        return new String(toByteArray(input), charset);
    }

    public static String toString(Reader input) throws IOException {
        return new String(toByteArray(input));
    }

    public static String toString(Reader input, String charset) throws IOException {
        return new String(toByteArray(input), charset);
    }

    public static String toString(Reader input, Charset charset) throws IOException {
        return new String(toByteArray(input), charset);
    }

    public static String toString(byte[] byteArray) {
        return new String(byteArray);
    }

    public static String toString(byte[] byteArray, String charset) {
        return toString(byteArray, Charset.forName(charset));
    }

    public static String toString(byte[] byteArray, Charset charset) {
        return new String(byteArray, charset);
    }

    public static byte[] toByteArray(CharSequence input) {
        if (input == null) {
            return new byte[0];
        }
        return input.toString().getBytes();
    }

    public static byte[] toByteArray(CharSequence input, String charset) {
        return toByteArray(input, Charset.forName(charset));
    }

    public static byte[] toByteArray(CharSequence input, Charset charset) {
        if (input == null) {
            return new byte[0];
        } else {
            return input.toString().getBytes(charset);
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(input, output);
        output.close();
        return output.toByteArray();
    }

    public static byte[] toByteArray(InputStream input, int size) throws IOException {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
        }

        if (size == 0) {
            return new byte[0];
        }

        byte[] data = new byte[size];
        int offset = 0;
        int byteCount;
        while ((offset < size) && (byteCount = input.read(data, offset, size - offset)) != -1) {
            offset += byteCount;
        }

        if (offset != size) {
            throw new IOException("Unexpected byte count size. current: " + offset + ", excepted: " + size);
        }
        return data;
    }

    public static byte[] toByteArray(Reader input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(input, output);
        output.close();
        return output.toByteArray();
    }

    public static byte[] toByteArray(Reader input, String charset) throws IOException {
        return toByteArray(input, Charset.forName(charset));
    }

    public static byte[] toByteArray(Reader input, Charset charset) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(input, output, charset);
        output.close();
        return output.toByteArray();
    }

    public static char[] toCharArray(CharSequence input) throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        write(output, input);
        return output.toCharArray();
    }

    public static char[] toCharArray(InputStream input) throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        write(input, output);
        return output.toCharArray();
    }

    public static char[] toCharArray(InputStream input, String charset) throws IOException {
        return toCharArray(input, Charset.forName(charset));
    }

    public static char[] toCharArray(InputStream input, Charset charset) throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        write(input, output, charset);
        return output.toCharArray();
    }

    public static char[] toCharArray(Reader input) throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        write(input, output);
        return output.toCharArray();
    }

    public static List<String> readLines(InputStream input, String charset) throws IOException {
        return readLines(input, Charset.forName(charset));
    }

    public static List<String> readLines(InputStream input, Charset charset) throws IOException {
        Reader reader = new InputStreamReader(input, charset);
        return readLines(reader);
    }

    public static List<String> readLines(InputStream input) throws IOException {
        Reader reader = new InputStreamReader(input);
        return readLines(reader);
    }

    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = toBufferedReader(input);
        List<String> list = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

    public static void write(OutputStream output, byte[] data) throws IOException {
        if (data != null) {
            output.write(data);
            output.flush();
        }
    }

    public static void write(Writer output, byte[] data) throws IOException {
        if (data != null) {
            output.write(new String(data));
            output.flush();
        }
    }

    public static void write(Writer output, byte[] data, String charset) throws IOException {
        write(output, data, Charset.forName(charset));
    }

    public static void write(Writer output, byte[] data, Charset charset) throws IOException {
        if (data != null) {
            output.write(new String(data, charset));
            output.flush();
        }
    }

    public static void write(Writer output, char[] data) throws IOException {
        if (data != null) {
            output.write(data);
            output.flush();
        }
    }

    public static void write(OutputStream output, char[] data) throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes());
            output.flush();
        }
    }

    public static void write(OutputStream output, char[] data, String charset) throws IOException {
        write(output, data, Charset.forName(charset));
    }

    public static void write(OutputStream output, char[] data, Charset charset) throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes(charset));
            output.flush();
        }
    }

    public static void write(Writer output, CharSequence data) throws IOException {
        if (data != null) {
            output.write(data.toString());
            output.flush();
        }
    }

    public static void write(OutputStream output, CharSequence data) throws IOException {
        if (data != null) {
            output.write(data.toString().getBytes());
            output.flush();
        }
    }

    public static void write(OutputStream output, CharSequence data, String charset) throws IOException {
        write(output, data, Charset.forName(charset));
    }

    public static void write(OutputStream output, CharSequence data, Charset charset) throws IOException {
        if (data != null) {
            output.write(data.toString().getBytes(charset));
            output.flush();
        }
    }

    public static void write(Reader input, OutputStream output) throws IOException {
        Writer out = new OutputStreamWriter(output);
        write(input, out);
    }

    public static void write(InputStream input, OutputStream output) throws IOException {
        int len;
        byte[] buffer = new byte[4096];
        while ((len = input.read(buffer)) != -1) {
            output.write(buffer, 0, len);
            output.flush();
        }
    }

    public static void write(InputStream input, Writer output) throws IOException {
        Reader in = new InputStreamReader(input);
        write(in, output);
    }

    public static void write(Reader input, OutputStream output, String charset) throws IOException {
        write(input, output, Charset.forName(charset));
    }

    public static void write(Reader input, OutputStream output, Charset charset) throws IOException {
        Writer out = new OutputStreamWriter(output, charset);
        write(input, out);
    }

    public static void write(InputStream input, OutputStream output, String charset) throws IOException {
        write(input, output, Charset.forName(charset));
    }

    public static void write(InputStream input, OutputStream output, Charset charset) throws IOException {
        Reader in = new InputStreamReader(input, charset);
        write(in, output);
    }

    public static void write(InputStream input, Writer output, String charset) throws IOException {
        write(input, output, Charset.forName(charset));
    }

    public static void write(InputStream input, Writer output, Charset charset) throws IOException {
        Reader in = new InputStreamReader(input, charset);
        write(in, output);
    }

    public static void write(Reader input, Writer output) throws IOException {
        int len;
        char[] buffer = new char[4096];
        while (-1 != (len = input.read(buffer))) {
            output.write(buffer, 0, len);
            output.flush();
        }
    }

    public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
        input1 = toBufferedInputStream(input1);
        input2 = toBufferedInputStream(input2);

        int ch = input1.read();
        while (-1 != ch) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }
        int ch2 = input2.read();
        return ch2 == -1;
    }

    public static boolean contentEquals(Reader input1, Reader input2) throws IOException {
        input1 = toBufferedReader(input1);
        input2 = toBufferedReader(input2);

        int ch = input1.read();
        while (-1 != ch) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        int ch2 = input2.read();
        return ch2 == -1;
    }

    public static boolean contentEqualsIgnoreEOL(Reader input1, Reader input2) throws IOException {
        BufferedReader br1 = toBufferedReader(input1);
        BufferedReader br2 = toBufferedReader(input2);

        String line1 = br1.readLine();
        String line2 = br2.readLine();
        while ((line1 != null) && (line2 != null) && (line1.equals(line2))) {
            line1 = br1.readLine();
            line2 = br2.readLine();
        }
        return line1 != null && (line2 == null || line1.equals(line2));
    }

    /**
     * Access to a directory available size.
     *
     * @param path path.
     *
     * @return space size.
     */
    public static long getDirSize(String path) {
        StatFs stat;
        try {
            stat = new StatFs(path);
        } catch (Exception e) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        } else {
            return (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        }
    }

    /**
     * If the folder can be written.
     *
     * @param path path.
     *
     * @return True: success, or false: failure.
     */
    public static boolean canWrite(String path) {
        return new File(path).canWrite();
    }

    /**
     * If the folder can be readResponse.
     *
     * @param path path.
     *
     * @return True: success, or false: failure.
     */
    public static boolean canRead(String path) {
        return new File(path).canRead();
    }

    /**
     * Create a folder, If the folder exists is not created.
     *
     * @param folderPath folder path.
     *
     * @return True: success, or false: failure.
     */
    public static boolean createFolder(String folderPath) {
        if (!TextUtils.isEmpty(folderPath)) {
            File folder = new File(folderPath);
            return createFolder(folder);
        }
        return false;
    }

    /**
     * Create a folder, If the folder exists is not created.
     *
     * @param targetFolder folder path.
     *
     * @return True: success, or false: failure.
     */
    public static boolean createFolder(File targetFolder) {
        if (targetFolder.exists()) {
            if (targetFolder.isDirectory()) {
                return true;
            }
            //noinspection ResultOfMethodCallIgnored
            targetFolder.delete();
        }
        return targetFolder.mkdirs();
    }

    /**
     * Create a folder, If the folder exists is not created.
     *
     * @param folderPath folder path.
     *
     * @return True: success, or false: failure.
     */
    public static boolean createNewFolder(String folderPath) {
        return delFileOrFolder(folderPath) && createFolder(folderPath);
    }

    /**
     * Create a folder, If the folder exists is not created.
     *
     * @param targetFolder folder path.
     *
     * @return True: success, or false: failure.
     */
    public static boolean createNewFolder(File targetFolder) {
        return delFileOrFolder(targetFolder) && createFolder(targetFolder);
    }

    /**
     * Create a file, If the file exists is not created.
     *
     * @param filePath file path.
     *
     * @return True: success, or false: failure.
     */
    public static boolean createFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            return createFile(file);
        }
        return false;
    }

    /**
     * Create a file, If the file exists is not created.
     *
     * @param targetFile file.
     *
     * @return True: success, or false: failure.
     */
    public static boolean createFile(File targetFile) {
        if (targetFile.exists()) {
            if (targetFile.isFile()) {
                return true;
            }
            delFileOrFolder(targetFile);
        }
        try {
            return targetFile.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Create a new file, if the file exists, delete and create again.
     *
     * @param filePath file path.
     *
     * @return True: success, or false: failure.
     */
    public static boolean createNewFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            return createNewFile(file);
        }
        return false;
    }

    /**
     * Create a new file, if the file exists, delete and create again.
     *
     * @param targetFile file.
     *
     * @return True: success, or false: failure.
     */
    public static boolean createNewFile(File targetFile) {
        if (targetFile.exists()) {
            delFileOrFolder(targetFile);
        }
        try {
            return targetFile.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Delete file or folder.
     *
     * @param path path.
     *
     * @return is succeed.
     *
     * @see #delFileOrFolder(File)
     */
    public static boolean delFileOrFolder(String path) {
        return delFileOrFolder(new File(path));
    }

    /**
     * Delete file or folder.
     *
     * @param file file.
     *
     * @return is succeed.
     *
     * @see #delFileOrFolder(String)
     */
    public static boolean delFileOrFolder(File file) {
        if (file == null || !file.exists()) {
            // do nothing
        } else if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File sonFile: files) {
                    delFileOrFolder(sonFile);
                }
            }
            file.delete();
        }
        return true;
    }

    private static class NonClosingInputStream extends FilterInputStream {

        public NonClosingInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
        }
    }

    private static class NonClosingOutputStream extends FilterOutputStream {

        public NonClosingOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(byte[] b, int off, int let) throws IOException {
            // It is critical that we override this method for performance.
            out.write(b, off, let);
        }

        @Override
        public void close() throws IOException {
        }
    }
}