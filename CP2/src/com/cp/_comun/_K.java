package com.cp._comun;

import java.io.File;

/**
 * @author Emilio Estecha 2013
 *
 */
public class _K {
	
	public static final String unidadIntercambio = "c:";
	
	public static final String caminoInterfaz    = unidadIntercambio +File.separator+"datos"+File.separator+"SisExt"+File.separator;
	public static final String caminoSalida      = caminoInterfaz + "llamadas" + File.separator;
	public static final String caminoEntrada     = caminoInterfaz + "retornos" + File.separator;
	public static final String caminoExecExterno = caminoInterfaz + "exec"     + File.separator;
	
	public static final String ejecutableExterno = "SE.bat";
	
	public static final String extFicParm = ".txt";
	
	public static final String sepFld = "\t";
	public static final String sepReg = "\r\n";
	public static final String sepReg_0x0D = "\r";
	public static final String sepReg_0x0A = "\n";
}
