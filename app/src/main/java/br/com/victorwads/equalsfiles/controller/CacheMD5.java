/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.victorwads.equalsfiles.controller;

// <editor-fold defaultstate="collapsed" desc="Imports">

import br.com.victorwads.equalsfiles.model.MD5Cache;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;
// </editor-fold>

/**
 * @author victo
 */
public class CacheMD5{

	private static final MD5Cache MD5BD = new MD5Cache();

	private static String convertByteArrayToHexString(byte[] arrayBytes){
		StringBuilder stringBuffer = new StringBuilder();
		for(int i = 0; i < arrayBytes.length; i++){
			stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
				.substring(1));
		}
		return stringBuffer.toString();
	}

	private static String hashFile(String file, String algorithm){
		try{
			FileInputStream inputStream = new FileInputStream(file);
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			byte[] bytesBuffer = new byte[1024];
			int bytesRead;

			while((bytesRead = inputStream.read(bytesBuffer)) != -1){
				digest.update(bytesBuffer, 0, bytesRead);
			}
			inputStream.close();
			byte[] hashedBytes = digest.digest();

			return convertByteArrayToHexString(hashedBytes);
		}catch(Exception e){
		}
		return null;
	}

	public static void addCache(MD5Cache.Join cache){
		if(MD5BD.get(cache.caminho) == null){
			MD5BD.put(cache.caminho, cache.md5);
		}
	}

	public static String registerMD5(String arquivo){
		String md5 = MD5BD.get(arquivo);
		if(md5 == null){
			md5 = hashFile(arquivo, "MD5");
			if(md5 == null){
				md5 = "";
			}else{
				addCache(new MD5Cache.Join(md5, arquivo));
			}
		}
		return md5;
	}

	public static String humamSize(long size){
		if(size == 0){
			return "0.00 B";
		}

		String[] s = {"B", "KB", "MB", "GB", "TB", "PB"};
		int e = (int) Math.floor(Math.log(size) / Math.log(1024));
		float f = (float) size / (float) Math.pow(1024, (double) e);

		return new DecimalFormat("#.##").format(f) + " " + s[e];
	}
}
