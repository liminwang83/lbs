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
            //�ļ�������classes�ĸ�·����
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
            //TODO: property�е����Ľ���ת�봦��
            value = new String( properties.getProperty( key ).getBytes( "ISO-8859-1" ), "GBK" );
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
        return value;
    }

    //��ȡproperties��ȫ����Ϣ
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

    //д��properties��Ϣ
    public static void writeProperties( String filePath, String parameterName, String parameterValue )
    {
        Properties prop = new Properties();
        try
        {
            InputStream fis = new FileInputStream( filePath );
            //���������ж�ȡ�����б�����Ԫ�ضԣ�
            prop.load( fis );
            //���� Hashtable �ķ��� put��ʹ�� getProperty �����ṩ�����ԡ�
            //ǿ��Ҫ��Ϊ���Եļ���ֵʹ���ַ���������ֵ�� Hashtable ���� put �Ľ����
            OutputStream fos = new FileOutputStream( filePath );
            prop.setProperty( parameterName, parameterValue );
            //���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ��
            //���� Properties ���е������б�����Ԫ�ضԣ�д�������
            prop.store( fos, "Update '" + parameterName + "' value" );
        }
        catch( IOException e )
        {
            System.err.println( "Visit " + filePath + " for updating " + parameterName + " value error" );
        }
    }

}
