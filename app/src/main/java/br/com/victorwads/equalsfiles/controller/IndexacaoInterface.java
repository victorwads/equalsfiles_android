/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.victorwads.equalsfiles.controller;

/**
 *
 * @author victo
 */
public interface IndexacaoInterface {

	/**
	 *
	 */
	void clear();

	/**
	 *
	 * @param i
	 */
	void setLoadingTotal(int i);

	/**
	 *
	 * @param i
	 */
	void loading(int i);

	/**
	 *
	 * @param path
	 * @param i
	 */
	void loading(String path, int i);

	/**
	 *
	 * @param info
	 * @param infinita
	 */
	void loading(String info, boolean infinita);

	/**
	 *
	 * @param segundos
	 */
	void setDuration(int segundos);

	/**
	 *
	 */
	void finish();

}
