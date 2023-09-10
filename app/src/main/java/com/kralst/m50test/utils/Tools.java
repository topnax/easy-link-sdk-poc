package com.kralst.m50test.utils;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.zip.CRC32;

/**
 * Created by huangwp on 2017/11/9. (taken from EasyLink SDK sample)
 */

public class Tools {
   private static final String TAG = "Tools";
   private static final String UTF8 = "UTF-8";
   private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   private Tools() {

   }

   public static void delay(int milliSeconds) {
      SystemClock.sleep(milliSeconds);
   }

   public static byte[] string2Bytes(String source) {
      byte[] result = new byte[0];
      try {
         if (source != null)
            result = source.getBytes(UTF8);
      } catch (UnsupportedEncodingException e) {
         //ignore the exception
         Log.e(TAG, "", e);
      }
      return result;
   }

   public static byte[] string2Bytes(String source, int checkLen) {
      byte[] result = new byte[0];
      if (source == null || source.length() != checkLen)
         return result;
      try {
         result = source.getBytes(UTF8);
      } catch (UnsupportedEncodingException e) {
         //ignore the exception
         Log.e(TAG, "", e);
      }
      return result;
   }

   public static String bcd2Str(byte[] b) {
      if (b == null) {
         return null;
      }
      StringBuilder sb = new StringBuilder(b.length * 2);
      for (byte i : b) {
         sb.append(HEX_DIGITS[((i & 0xF0) >>> 4)]);
         sb.append(HEX_DIGITS[(i & 0xF)]);
      }

      return sb.toString();
   }

   public static String bcd2Str(byte[] b, int length) {
      if (b == null) {
         return null;
      }
      StringBuilder sb = new StringBuilder(length * 2);
      for (int i = 0; i < length; ++i) {
         sb.append(HEX_DIGITS[((b[i] & 0xF0) >>> 4)]);
         sb.append(HEX_DIGITS[(b[i] & 0xF)]);
      }

      return sb.toString();
   }

   private static int strByte2Int(byte b) {
      int j;
      if ((b >= 'a') && (b <= 'z')) {
         j = b - 'a' + 0x0A;
      } else {
         if ((b >= 'A') && (b <= 'Z'))
            j = b - 'A' + 0x0A;
         else
            j = b - '0';
      }
      return j;
   }

   public static String getPaddedNumber(long num, int digit) {
      NumberFormat nf = NumberFormat.getInstance();
      nf.setGroupingUsed(false);
      nf.setMaximumIntegerDigits(digit);
      nf.setMinimumIntegerDigits(digit);
      return nf.format(num);
   }

   public static byte[] str2Bcd(String asc) {
      String str = asc;
      if (str.length() % 2 != 0) {
         str = "0" + str;
      }
      int len = str.length();
      if (len >= 2) {
         len /= 2;
      }
      byte[] bbt = new byte[len];
      byte[] abt = str.getBytes();

      for (int p = 0; p < str.length() / 2; p++) {
         bbt[p] = (byte) ((strByte2Int(abt[(2 * p)]) << 4) + strByte2Int(abt[(2 * p + 1)]));
      }
      return bbt;
   }

   public static byte[] int2ByteArray(int i) {
      byte[] to = new byte[4];
      int offset = 0;
      to[offset] = (byte) (i >>> 24 & 0xFF);
      to[(offset + 1)] = (byte) (i >>> 16 & 0xFF);
      to[(offset + 2)] = (byte) (i >>> 8 & 0xFF);
      to[(offset + 3)] = (byte) (i & 0xFF);
      for (int j = 0; j < to.length; ++j) {
         if (to[j] != 0) {
            return Arrays.copyOfRange(to, j, to.length);
         }
      }
      return new byte[]{0x00};
   }

   public static void int2ByteArray(int i, byte[] to, int offset) {
      to[offset] = (byte) (i >>> 24 & 0xFF);
      to[(offset + 1)] = (byte) (i >>> 16 & 0xFF);
      to[(offset + 2)] = (byte) (i >>> 8 & 0xFF);
      to[(offset + 3)] = (byte) (i & 0xFF);
   }

   public static void int2ByteArrayLittleEndian(int i, byte[] to, int offset) {
      to[offset] = (byte) (i & 0xFF);
      to[(offset + 1)] = (byte) (i >>> 8 & 0xFF);
      to[(offset + 2)] = (byte) (i >>> 16 & 0xFF);
      to[(offset + 3)] = (byte) (i >>> 24 & 0xFF);
   }

   public static void short2ByteArray(short s, byte[] to, int offset) {
      to[offset] = (byte) (s >>> 8 & 0xFF);
      to[(offset + 1)] = (byte) (s & 0xFF);
   }

   public static void short2ByteArrayLittleEndian(short s, byte[] to, int offset) {
      to[offset] = (byte) (s & 0xFF);
      to[(offset + 1)] = (byte) (s >>> 8 & 0xFF);
   }

   public static int byteArray2Int(byte[] from, int offset) {
      return from[offset] << 24 & 0xFF000000 | from[(offset + 1)] << 16 & 0xFF0000 |
              from[(offset + 2)] << 8 & 0xFF00 | from[(offset + 3)] & 0xFF;
   }

   public static int byteArray2IntLittleEndian(byte[] from, int offset) {
      return from[(offset + 3)] << 24 & 0xFF000000 | from[(offset + 2)] << 16 & 0xFF0000 |
              from[(offset + 1)] << 8 & 0xFF00 | from[offset] & 0xFF;
   }

   public static short byteArray2Short(byte[] from, int offset) {
      return (short) (from[offset] << 8 & 0xFF00 | from[(offset + 1)] & 0xFF);
   }

   public static short byteArray2ShortLittleEndian(byte[] from, int offset) {
      return (short) (from[(offset + 1)] << 8 & 0xFF00 | from[offset] & 0xFF);
   }

   public static long getCRC32(byte[] data) {
      CRC32 crc = new CRC32();
      crc.update(data);
      return crc.getValue();
   }

   public static int bytes2Int(byte[] buffer, int radix) {
      int result;
      try {
         result = Integer.parseInt(bytes2String(buffer), radix);
      } catch (NumberFormatException e) {
         //ignore the exception
         Log.e(TAG, "Exception:", e);
         result = 0;
      }
      return result;
   }

   public static String bytes2String(byte[] source) {
      String result = "";
      try {
         if (source.length > 0)
            result = new String(source, UTF8);
      } catch (UnsupportedEncodingException e) {
         //ignore the exception
         Log.e(TAG, "", e);
         result = "";
      }
      return result;
   }

   public static int bytes2Int(byte[] buffer) {
      int result = 0;
      int len = buffer.length;

      if ((len <= 0) || (len > 4)) {
         return 0;
      }
      for (int i = 0; i < len; i++) {
         result += (byte2Int(buffer[i]) << 8 * (len - 1 - i));
      }

      return result;
   }

   public static int byte2Int(byte b) {
      return b & 0xFF;
   }

   public static <T extends Enum<T>> T getEnum(Class<T> clazz, int ordinal) {
      for (T t : clazz.getEnumConstants()) {
         if (t.ordinal() == ordinal) {
            return t;
         }
      }
      return null;
   }

   public static byte[] fillData(int dataLength, byte[] source, int offset) {
      byte[] result = new byte[dataLength];
      if (offset >= 0)
         System.arraycopy(source, 0, result, offset, source.length);
      return result;
   }

   public static byte[] fillData(int dataLength, byte[] source, int offset, byte fillByte) {
      byte[] result = new byte[dataLength];
      for (int i = 0; i < dataLength; i++) {
         result[i] = fillByte;
      }
      if (offset >= 0)
         System.arraycopy(source, 0, result, offset, source.length);
      return result;
   }

   public static boolean byte2Boolean(byte b) {
      return b != 0;
   }

   public static byte boolean2Byte(boolean b) {
      return (byte) (b ? 1 : 0);
   }

   public static byte[] trimTailChars(byte[] data, byte removeChar){
      int len = data.length;
      int removeCnt = 0;
      int i = 0;

      for(i = len - 1; i >= 0; i--){
         if(data[i] != removeChar){
            break;
         }
         removeCnt++;
      }

      if(data[len - 1] == removeChar){
         return Arrays.copyOfRange(data ,0, len - removeCnt );
      }
      return data;
   }

   public static void mkDir(String folderPath) {
      File folder = new File(folderPath);
      if (!folder.exists()) {
         folder.mkdirs();
      }
   }
   public static boolean copyFileFromAssert(Context ctx, String srcFile, String descFile) {
      Log.d(TAG, " source file:" + srcFile + ", des file:" + descFile);

      boolean bIsSuc = true;
      InputStream iptStm = null;
      OutputStream optStm = null;

      try {
         iptStm = ctx.getAssets().open(srcFile);
         Log.d(TAG, "AssetsFilePath:" + srcFile + " FileSize:" + (iptStm == null ? 0 : iptStm.available()));
         Log.d(TAG, "strDesFilePath:" + descFile);

         if (iptStm == null) {
            Log.d(TAG, "file [" + srcFile + "]not exist is assets!");
            return true;
         }

         File file = new File(descFile);
         if (!file.exists()) {//target file not exist, need copy
            file.createNewFile();
            Runtime.getRuntime().exec("chmod 766 " + file);
         } else {
            if (file.length() == iptStm.available()) {
               Log.d(TAG, "same file, no need to copy!");
               iptStm.close();
               return true;
            }
            file.delete();
            file.createNewFile();
            Runtime.getRuntime().exec("chmod 766 " + file);
         }

         optStm = new FileOutputStream(file);

         int nLen = 0;

         byte[] buff = new byte[1024 * 1];
         while ((nLen = iptStm.read(buff)) > 0) {
            optStm.write(buff, 0, nLen);
         }

         if (optStm != null) {
            optStm.close();
         }

         if (iptStm != null) {
            iptStm.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
         bIsSuc = false;
      } finally {
         return bIsSuc;
      }
   }
}
