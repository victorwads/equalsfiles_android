/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.victorwads.equalsfiles.controller;

import br.com.victorwads.equalsfiles.dao.Relatorio;
import br.com.victorwads.equalsfiles.model.Resultado;

import java.util.Date;

/**
 * @author victo
 */
public class Resultados{

	public static boolean inserir(Resultado resultado){
		return new Relatorio().registrar(resultado);
	}

	public static Resultado byId(int id){
		Relatorio con = new Relatorio();
		con.setAutoClose(false);
		Resultado r = con.listar(id);
		con.carregarArquivos(r);
		con.closeConnection();
		return r;
	}

	public static Resultado[] listar(Date min, Date max){
		return new Relatorio().listar(min, max);
	}

	public static void carregarArquivos(Resultado resultado){
		new Relatorio().carregarArquivos(resultado);
	}

	public static boolean excluir(Resultado resultado){
		return new Relatorio().excluir(resultado);
	}
}
