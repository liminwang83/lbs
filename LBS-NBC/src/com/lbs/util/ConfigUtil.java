package com.lbs.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;

public class ConfigUtil
{

    /**
     * @param args
     */
    public static void main( String[] args )
    {
        // TODO Auto-generated method stub
        System.out.println( getProperty( "training.docs.dir" ) );
    }

    public static String getProperty( String key )
    {
        Properties properties = new Properties();
        try
        {
            //文件被放在classes的根路径下
            InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream( "Config.properties" );
            properties.load( in );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        String value = null;
        try
        {
            //TODO: property中的中文进行转码处理
            value = new String( properties.getProperty( key ).getBytes( "ISO-8859-1" ), "GBK" );
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
        return value;
    }

    //读取properties的全部信息
    public static void readProperties( String filePath )
    {
        Properties props = new Properties();
        try
        {
            InputStream in = new BufferedInputStream( new FileInputStream( filePath ) );
            props.load( in );
            Enumeration en = props.propertyNames();
            while( en.hasMoreElements() )
            {
                String key = ( String ) en.nextElement();
                String Property = props.getProperty( key );
                System.out.println( key + Property );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    //写入properties信息
    public static void writeProperties( String filePath, String parameterName, String parameterValue )
    {
        Properties prop = new Properties();
        try
        {
            InputStream fis = new FileInputStream( filePath );
            //从输入流中读取属性列表（键和元素对）
            prop.load( fis );
            //调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
            //强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
            OutputStream fos = new FileOutputStream( filePath );
            prop.setProperty( parameterName, parameterValue );
            //以适合使用 load 方法加载到 Properties 表中的格式，
            //将此 Properties 表中的属性列表（键和元素对）写入输出流
            prop.store( fos, "Update '" + parameterName + "' value" );
        }
        catch( IOException e )
        {
            System.err.println( "Visit " + filePath + " for updating " + parameterName + " value error" );
        }
    }

}
