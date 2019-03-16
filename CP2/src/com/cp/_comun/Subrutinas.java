package com.cp._comun;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.aeat.valida.Validador;
import com.cp.FuentesDeDatos.BDConexion;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author Emilio Estecha 2014
 *
 */
public class Subrutinas {
	private final static String tag = "Subrutinas";	//this.getClass().getSimpleName();

	
	////////////////////////////////////
//	Para DESPLEGAR Billin:
	public static String m_modoEjecucion = "test";	// "test" / "real". En real lanza la "Autenticacion_FS()" 
	public static boolean m_isPool 		 = false;	// false  / true
	////////////////////////////////////

	private static final int g_filas_DSPFIL = 15;

	public static String archivo_config = "com.cp.struts.Config";
	public static String archivo_es     = "com.cp.struts.ApplicationResource";
	public static String archivo_en     = "com.cp.struts.ApplicationResource";

	public Subrutinas() {
//		String msgId  = "configuracion.modo";
//		m_modoEjecucion = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
	}
	////////////////////////
	public boolean controlAcceso(BDConexion dbConn, String usr, String pgm) {
		boolean resultado = false;
		///////////////////////////////////////////////////
		if ( usr == null || usr.trim().length() < 1 || pgm == null || pgm.trim().length() < 1 ) return resultado;
		if ( "null".equalsIgnoreCase( usr ) ) return resultado;
		if ( "null".equalsIgnoreCase( pgm ) ) return resultado;
		///////////////////////////////////////
		// ..acceso a BD para comprobar si el usuario contempla la acci�n...

		// Ejemplo:
		//        com.sl.se_SeguridadUsuarioProgramas.db.SeAccesoBaseDatos db = new com.hh.se_SeguridadUsuarioProgramas.db.SeAccesoBaseDatos();
		//        com.hh.se_SeguridadUsuarioProgramas.bean.SeBean    reg_Se = new com.hh.se_SeguridadUsuarioProgramas.bean.SeBean();
		//        reg_Se.setSe_ANADVN( usr ); reg_Se.setSe_ANBATX( pgm );
		//        try {
		//            reg_Se = db.se_getRcd( dbConn, reg_Se );
		//            if (reg_Se!=null)
		resultado = true;
		//        } catch (StExcepcion ex) {;}
		///////////////////////////////////////////////////
		return resultado;
	}
	public void Autenticacion_FS() {
		/////////////////////////////////////////
		// En OR: 2012/05/25 Se sospecha que �sto provoca "cuelgues" e impide el resto del proceso.
		// La soluci�n ha sido incluirlo en un servlet de "startup". 
		// Por ejemplo, as� quedar�a en el "web.xml" con orden de inicio '0':
		
//    <servlet>
//        <servlet-name>atmStartUp</servlet-name>
//        <servlet-class>com.tm.__main.startup.atmStartUp</servlet-class>
//        <load-on-startup>0</load-on-startup>
//    </servlet>
//    <servlet-mapping>
//        <servlet-name>atmStartUp</servlet-name>
//        <url-pattern>/atmStartUp</url-pattern>
//    </servlet-mapping>
	
	// Y en el m�todo "init()" del sevlet referido, se incluye la llamada:
		
//	public void init() {
//		try { super.init(); } catch (ServletException ex) { ex.printStackTrace(); }
//		com.tm._comun.Subrutinas subrut = new com.tm._comun.Subrutinas();
//		subrut.Autenticacion_FS();
//    }

		System.out.println("**** Autenticacion_FS() >>>>>>");
		/////////////////////
		BDConexion bd = null;
		try {
			bd = new BDConexion();
			if ( bd != null ) {
				String camino_red = getDBValueFromKey( bd, "SEG_pathDocs_NETUSE" );
				String usr_red    = getDBValueFromKey( bd, "SEG_pathDocs_USR");
				String pwd_red    = getDBValueFromKey( bd, "SEG_pathDocs_PWD");
				String strError   = "";
				try {
					System.out.println( "Aportando credenciales para " + camino_red );
					strError = logonRecursoRedWindows_sincro(camino_red, usr_red, pwd_red);
					if ( strError != null && strError.trim().length() > 01 ) {
						System.out.println( strError );
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		} catch (StExcepcion ex) {
			ex.printStackTrace();
		}
		/////////////////////
		System.out.println("**** Autenticacion_FS() <<<<<<");
	}
	public static void sincroSesion_COMUN(HttpServletRequest request, ActionForm pantalla) {
		;
	}
	public String logonRecursoRedWindows_sincro(String camino, String usuario, String password) throws IOException, InterruptedException {

		/////////////////////////////////////////
		// En OR: 2012/05/25 Se sospecha que �sto provoca "cuelgues" e impide el resto del proceso.
		// Ver nota en 'Autenticacion_FS()'...
		/////////////////////////////////////////

		Process process = null;
		String strError = "";
		String strOutput = "";
		String comando = "cmd.exe /c NET USE " + camino + " " + password + " /USER:" + usuario;

		process = (Runtime.getRuntime()).exec( comando );

		if ( process != null ) {
			///////////////////////
			// Canales de entrada/salida del proceso:
			String line;
			java.io.BufferedReader ir = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
			java.io.BufferedReader er = new java.io.BufferedReader(new java.io.InputStreamReader(process.getErrorStream()));

			while ((line = er.readLine()) != null) {
				System.out.println(line);
				strError += line + '\n';
			}
			while ((line = ir.readLine()) != null) {
				System.out.println(line);
				strOutput += line + '\n';
			}

			ir.close();
			er.close();
			process.waitFor();
			process.destroy();
			///////////////////////
		}
		return strError;
	}
	////////////////////////
	public static String run_comando_sincro( String[] params ) throws StExcepcion {
		String strError = "";
		String strOutput = "";
		Process process = null;
		try {
			process = new ProcessBuilder( params ).start();
			if ( process != null ) {
				///////////////////////
				// Canales de entrada/salida del proceso:
				String line;
				java.io.BufferedReader ir = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
				java.io.BufferedReader er = new java.io.BufferedReader(new java.io.InputStreamReader(process.getErrorStream()));

				while ((line = er.readLine()) != null) {
					// System.err.println(line);
					strError += line + '\n';
				}
				while ((line = ir.readLine()) != null) {
					// System.out.println(line);
					strOutput += line + '\n';
				}

				ir.close();
				er.close();
				process.waitFor();
				process.destroy();
				
				if ( strError != null && strError.trim().length() > 0 ) {
					throw new StExcepcion( strError );
				}
				
				///////////////////////
			}
	    	
		} catch (IOException e) { 
			throw new StExcepcion( e.getMessage() ); 
		} catch (InterruptedException e) {
			throw new StExcepcion( e.getMessage() ); 
		}

		return strOutput;
	}
	////////////////////////
	public BDConexion getBDConexion(HttpServletRequest request) {
		/////////////////////////
		BDConexion bd = (BDConexion) request.getSession().getAttribute("BDConexion");
		try {
			if ( bd == null || bd.getConexion().isClosed() ) {
				try {bd = new BDConexion();} catch (StExcepcion ex) {;}
			}
		} catch (SQLException ex) {;}

		request.getSession(true).setAttribute( "BDConexion", bd );
		return bd;
		/////////////////////////
	}
	////////////////////////
	public static BeanParaTablaHash[] cargarCombo_FilasGrid(){
		BeanParaTablaHash[] lista = new BeanParaTablaHash[3];
		lista[0] = new BeanParaTablaHash("15","15 filas");
		lista[1] = new BeanParaTablaHash("30","30 filas");
		lista[2] = new BeanParaTablaHash("50","50 filas");
		return lista;
	}
	public static BeanParaTablaHash[] cargarCombo_entidad(StBean[] Regs, String fieldKeyName, String fieldValueName) {
		// Esta funci�n genera una lista (posible para alimentar un combo) 
		// usando los campos indicados en par�metros como KEY y como VALUE.
		BeanParaTablaHash[] lista = null;
		if (Regs!=null) {
			lista = new BeanParaTablaHash[Regs.length + 1];
			lista[0] = new BeanParaTablaHash("","...");
			int i = 1;
			Class<?> clase = null;
			for( StBean reg : Regs ) {
				clase = reg.getClass();
				Object valor_K = null;
				Object valor_V = null;
				try {
					valor_K = clase.getField( fieldKeyName   ).get(reg);
					valor_V = clase.getField( fieldValueName ).get(reg);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				if ( valor_K != null && valor_V != null ) {
					lista[i] = new BeanParaTablaHash( valor_K.toString(), valor_K.toString() + " " + valor_V.toString());
				} else {
					lista[i] = new BeanParaTablaHash("","error al genearar lista...");
				}
				i++;
			}
		}
		if (lista==null) { lista = new BeanParaTablaHash[1]; }
		if (Regs==null || Regs.length<1) { lista[0] = new BeanParaTablaHash("","Sin datos"); }
		return lista;
	}
	////////////////////////
	public static int getG_filas_DSPFIL() {
		return g_filas_DSPFIL;
	}
	public static String getG_DB_LIBDAT(String db) {
		String msgId = "configuracion." + db + ".LIBDAT." + m_modoEjecucion;
		String r = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
		if (r == null) {
			System.out.println("Error en archivo '" + Subrutinas.archivo_config + "' en key: '" + msgId + "'");
		}
		return r;
	}
	public static String getG_DB_DRIVER(String db) {
		String msgId = "configuracion." + db + ".DRIVER." + m_modoEjecucion;
		String r = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
		if (r == null) {
			System.out.println("Error en archivo '" + Subrutinas.archivo_config + "' en key: '" + msgId + "'");
		}
		return r;
	}
	public static String getG_DB_PRE_IP(String db) {
		String msgId = "configuracion." + db + ".PRE_IP." + m_modoEjecucion;
		String r = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
		if (r == null) {
			System.out.println("Error en archivo '" + Subrutinas.archivo_config + "' en key: '" + msgId + "'");
		}
		return r;
	}
	public static String getG_DB_IP(String db) {
		String msgId = "configuracion." + db + ".DIR_IP." + m_modoEjecucion;
		String r = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
		if (r == null) {
			System.out.println("Error en archivo '" + Subrutinas.archivo_config + "' en key: '" + msgId + "'");
		}
		return r;
	}
	public static String getG_DB_POS_IP(String db) {
		String msgId = "configuracion." + db + ".POS_IP." + m_modoEjecucion;
		String r = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
		if (r == null) {
			System.out.println("Error en archivo '" + Subrutinas.archivo_config + "' en key: '" + msgId + "'");
		}
		return r;
	}
	public static String getG_DB_USR(String db) {
		String msgId = "configuracion." + db + ".RMTUSR." + m_modoEjecucion;
		String r = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
		if (r == null) {
			System.out.println("Error en archivo '" + Subrutinas.archivo_config + "' en key: '" + msgId + "'");
		}
		return r;
	}
	public static String getG_DB_PWD(String db) {
		String msgId = "configuracion." + db + ".RMTPWD." + m_modoEjecucion;
		String r = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
		if (r == null) {
			System.out.println("Error en archivo '" + Subrutinas.archivo_config + "' en key: '" + msgId + "'");
		}
		return r;
	}
	public static String getG_DB_OPTIONS(String db) {
		String msgId = "configuracion." + db + ".OPTIONS." + m_modoEjecucion;
		String r = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
		if (r == null) {
			r = "";
//			System.out.println("Error en archivo '" + Subrutinas.archivo_config + "' en key: '" + msgId + "'");
		} else {
			r = "?" + r;
		}
		return r;
	}
	public static String getG_DB_RWUPPERCASE(String db) {
		String msgId = "configuracion." + db + ".RW.UPPERCASE";
		String r = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
		if (r == null) {
			System.out.println("Error en archivo '" + Subrutinas.archivo_config + "' en key: '" + msgId + "'");
		}
		return r;
	}
	public static String getG_DB_RWLIKE(String db) {
		String msgId = "configuracion." + db + ".RW.LIKE";
		String r = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
		if (r == null) {
			System.out.println("Error en archivo '" + Subrutinas.archivo_config + "' en key: '" + msgId + "'");
		}
		return r;
	}
	public static String getG_DB_RWANYPATTERN(String db) {
		String msgId = "configuracion." + db + ".RW.ANYPATTERN";
		String r = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
		if (r == null) {
			System.out.println("Error en archivo '" + Subrutinas.archivo_config + "' en key: '" + msgId + "'");
		}
		return r;
	}
	////////////////////////
	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}
	public static String padLeft(String s, int n) {
		return String.format("%1$#" + n + "s", s);
	}
	public static String padLeftCeros(long dato, int n) {
		return String.format("%0" + n + "d", dato);
	}
	////////////////////////
	public static int    parse_integer( String s ) {
		int res = 0;
		try { res = Integer.parseInt(s); } catch (NumberFormatException ex) {;}
		return res;
	}
	public static long   parse_long( String s ) {
		long res = 0;
		try { res = Long.parseLong(s); } catch (NumberFormatException ex) {;}
		return res;
	}
	public static double parse_double( String s ) {
		double res = 0.0;
		try { res = Double.parseDouble(s); } catch (NumberFormatException ex) {;}
		return res;
	}
	public static String bytesToHex(byte[] b) {
        char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                           '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuffer buf = new StringBuffer();
        for (int j=0; j<b.length; j++) {
           buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
           buf.append(hexDigit[b[j] & 0x0f]);
        }
        return buf.toString();
     }
    public static String getRandomHashCode() {
    	String resultado = "???";
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA1");
			// byte[] bytes = digest.digest( usr.getBytes() );	// Siewmpre devuelve el mismo valor.
			Integer pito = (int) (Math.random() * 10000);		// hashCode aleatorio
			byte[] bytes = digest.digest( pito.toString().getBytes() );
			resultado = Subrutinas.bytesToHex( bytes );
		} catch (NoSuchAlgorithmException e) {;}
		
		return resultado;
    }
    public static String getComputername() {
    	String computername = "SinNombre";
    	try { computername = InetAddress.getLocalHost().getHostName();} catch (UnknownHostException e) {;}
    	return computername;
    }
	public static String get_CORS_incomingURLs() {
		String msgId = "CORS.incomingURLs";
		String r = org.apache.struts.util.MessageResources.getMessageResources(Subrutinas.archivo_config).getMessage(msgId);
		if (r == null) {
			r = "";
			System.err.println("Error al recuperar del archivo de propiedades '" + Subrutinas.archivo_es + "' en key: '" + msgId + "'");
		}
		return r;
	}
    public static String getHashFromRandomCode() {
    	String resultado = "???";
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA1");
			// byte[] bytes = digest.digest( usr.getBytes() );	// Siewmpre devuelve el mismo valor.
			Integer pito = (int) (Math.random() * 10000);		// hashCode aleatorio
			byte[] bytes = digest.digest( pito.toString().getBytes() );
			resultado = Subrutinas.bytesToHex( bytes );
		} catch (NoSuchAlgorithmException e) {;}
		
		return resultado;
    }
    public static String getHashFromString( String dato ) {
    	String resultado = "getHashFromString_error";
    	
    	// GENERA UN HASH: "SHA-1"
    	// SHA-1 produces a 160-bit (20-byte) hash value
    	// El resultado es una cadena de 40 caracteres.
    	
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA1");
			byte[] bytes = digest.digest( dato.getBytes() );	// Siewmpre devuelve el mismo valor.
			resultado = Subrutinas.bytesToHex( bytes );
		} catch (NoSuchAlgorithmException e) {;}
		
		return resultado;
    }
    public static String getHashMD5FromString( String dato ) {
    	String resultado = "getHashMD5FromString_error";
    	
    	// GENERA UN HASH: "SHA-1"
    	// SHA-1 produces a 160-bit (20-byte) hash value
    	// El resultado es una cadena de 40 caracteres.
    	
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes = digest.digest( dato.getBytes() );	// Siewmpre devuelve el mismo valor.
			resultado = Subrutinas.bytesToHex( bytes );
		} catch (NoSuchAlgorithmException e) {;}
		
		return resultado;
    }
    public static boolean  NIF_isCorrecto( String NIF ) {
    	try {
			Validador validador = new Validador();
			if ( (validador.checkNif( NIF )) > 0) { 
				return true;
			}
		} catch (Exception e) {;}
    	
		return false;

    }
    //////////////////////
	// FICHEROS
	public static int ZIP_addFiles         ( final StringBuffer logVar_o_null, final String[] fileNamesList,  final String nombreZipFicheroCompleto ) {
		int resultado = 0;
		if(logVar_o_null!=null) logVar_o_null.append("\r\n" + "ZIP_addFiles( " + nombreZipFicheroCompleto + " ) >>>>>>>>>>>>>>");

		if ( fileNamesList == null || fileNamesList.length < 1 ) return resultado;

		try {
			File file = new File( nombreZipFicheroCompleto );
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			ZipOutputStream  zos = new ZipOutputStream(fos);
			try {
				for (int i = 0; i < fileNamesList.length; ++i) {
					byte[] bytes = readFileBin( logVar_o_null, fileNamesList[i] );
					try {
						if(logVar_o_null!=null) logVar_o_null.append("\r\n" + "ZIP_addFiles() ADD '" + fileNamesList[i] + "'...");
						zos.putNextEntry( new ZipEntry( fileNamesList[i] ) );
						zos.write(bytes);
						++resultado;
					} catch (IOException e) {
						if(logVar_o_null!=null) logVar_o_null.append("\r\n" + e.getMessage());
					} finally {
						zos.closeEntry();
						bytes = null;
					}
				}
			} catch (IOException e) {
				if(logVar_o_null!=null) logVar_o_null.append("\r\n" + e.getMessage());
			} finally {
				zos.close();
			}
		} catch (IOException e) {
			if(logVar_o_null!=null) logVar_o_null.append("\r\n" + e.getMessage());
		}
		if(logVar_o_null!=null) logVar_o_null.append("\r\n" + "ZIP_addFiles( " + nombreZipFicheroCompleto + " ) <<<<<<<<<<<<<<");
		return resultado;
	}
	public static int ZIP_extraerConFiltro ( final StringBuffer logVar_o_null, final String nombreZipFicheroCompleto, final String dirDestino, final String filtroDeNombres_patron) {
		int numExtraidos = 0;
		String patron = (filtroDeNombres_patron==null)?"":filtroDeNombres_patron.trim();
		FileInputStream fis = null;
		ZipInputStream zis = null;
		ZipEntry ze = null;
		File zipFile = new File( nombreZipFicheroCompleto );
		//////////////////////////////////////
		if ( zipFile.exists() && zipFile.canRead() ) {
			try {
				fis = new FileInputStream( zipFile );
				zis = new ZipInputStream( new BufferedInputStream(fis) );
				try {
					try {
						String nomFicComp = null;
						String filename  = null;
						while ((ze = zis.getNextEntry()) != null) {

							filename = ze.getName().replace('\\', '/');

							if ( !ze.isDirectory() && filename.indexOf( patron ) > -1 ) {
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								byte[] buffer = new byte[65536];
								int count;
								while ( (count = zis.read(buffer)) != -1 ) {
									baos.write(buffer, 0, count);
								}
								//////////////////////////////////////////
								nomFicComp = dirDestino + File.separator + filename;
								if ( grabFile( logVar_o_null, nomFicComp, baos.toByteArray() ) ) {
									if(logVar_o_null!=null) logVar_o_null.append("\r\n" + nomFicComp);
									numExtraidos++;
								}
								//////////////////////////////////////////
							}

						}
					} catch (IOException e) {
						if(logVar_o_null!=null) logVar_o_null.append("\r\n" + e.getMessage());
						e.printStackTrace();
					}
				} finally {
					try {
						if ( zis != null ) zis.close();
					} catch (IOException e) {
						if(logVar_o_null!=null) logVar_o_null.append("\r\n" + e.getMessage());
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e) {
				if(logVar_o_null!=null) logVar_o_null.append("\r\n" + e.getMessage());
				e.printStackTrace();
			}
		} else {
			if(logVar_o_null!=null) logVar_o_null.append("\r\n" + "El fichero \n\r\t" + nombreZipFicheroCompleto + "\n\r no existe o no se puede leer.");
		}
		//////////////////////////////////////
		return numExtraidos;
	}
	public static boolean grabFile         ( final StringBuffer logVar_o_null, final String nombreFicheroCompleto, final byte[] contenido ) {
		boolean resultado = false;

		String nomCamino = nombreFicheroCompleto.replace('\\', '/');
		int idx = nomCamino.lastIndexOf( '/' );
		if (idx > -1 ) nomCamino = nomCamino.substring( 0, idx );

		crtDir(logVar_o_null, nomCamino);

		FileOutputStream fos = null;
		try {
			File file = new File( nombreFicheroCompleto );
			file.delete();
			file.createNewFile();
			fos = new FileOutputStream(file);
			fos.write( contenido );
			resultado = true;
		} catch (FileNotFoundException e) {
			if(logVar_o_null!=null) logVar_o_null.append("\r\n" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			if(logVar_o_null!=null) logVar_o_null.append("\r\n" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					if(logVar_o_null!=null) logVar_o_null.append("\r\n" + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return resultado;
	}
	public static String  readFile         ( final StringBuffer logVar_o_null, final String nombreFicheroCompleto ) {
		String contenido = "";
		try {
			byte[] buff = readFileBin( logVar_o_null, nombreFicheroCompleto );
			if ( buff != null ) {
				contenido = new String( buff , "ISO-8859-1");
			}
		} catch (UnsupportedEncodingException e) {
			if(logVar_o_null!=null) logVar_o_null.append("\r\n" + e.getMessage());
		}
		return contenido;
	}
	public static byte[]  readFileBin      ( final StringBuffer logVar_o_null, final String nombreFicheroCompleto ) {

		byte[] bytes = null;

		File fichero = new File ( nombreFicheroCompleto );
		int lenFic = (int) fichero.length();
		bytes = new byte[ lenFic ];

		FileInputStream fis = null;
		DataInputStream dis = null;
		try {
			fis = new FileInputStream(fichero);
			dis = new DataInputStream( fis );
			dis.readFully(bytes, 0, lenFic);
		} catch (IOException e) {
			if(logVar_o_null!=null) logVar_o_null.append("\r\n" + e.getMessage());
		} finally {
			try { if (fis!=null) { fis.close(); } } catch (IOException e) {;}
		}

		return bytes;
	}
	public static boolean crtDir           ( final StringBuffer logVar_o_null, final String nombreDirectorio ) {
		boolean resultado = false;
		//////////////////////////////////
		File dir = new File( nombreDirectorio );
		if ( !dir.exists() ) dir.mkdirs();
		if ( dir.exists() && dir.canWrite() ) resultado = true;
		//////////////////////////////////
		return resultado;
	}
	public static File[] getFiles_endsWith ( File dir, final String sufijo ) {
		// Por ejemplo que acaban en ".pdf"
	    return dir.listFiles(new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return name.toLowerCase().endsWith( sufijo.toLowerCase() );
	        }
	    });
	}
	public static File[] getFiles_startsWith(File dir, final String prefijo ) {
		// Por ejemplo que empiezan por "123^321^"
	    return dir.listFiles(new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return name.toLowerCase().startsWith( prefijo.toLowerCase() );
	        }
	    });
	}	
	public static File[] getFiles_deNombre ( File dir, final String nombre ) {
		final String nomSinExt;
    	int idx = nombre.lastIndexOf('.');
    	if ( idx > -1 ) {
    		nomSinExt = nombre.substring(0, idx);
    	} else {
    		nomSinExt = nombre;
    	}
	    return dir.listFiles(new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	        	String nombreActualSinExt = name;
	        	int idx = name.lastIndexOf('.');
	        	if ( idx > -1 ) {
	        		nombreActualSinExt = name.substring(0, idx);
	        	}
	        	if ( nombreActualSinExt.equalsIgnoreCase( nomSinExt ) ) {
	        		return true;
	        	}
	            return false;
	        }
	    });
	}	
	public static void   copyFile( final String origen, final String destino) throws IOException {
        Path FROM = Paths.get(origen);
        Path TO = Paths.get(destino);
        //sobreescribir el fichero de destino, si existe, y copiar
        // los atributos, incluyendo los permisos rwx
        CopyOption[] options = new CopyOption[]{
          StandardCopyOption.REPLACE_EXISTING,
          StandardCopyOption.COPY_ATTRIBUTES
        };
        Files.copy(FROM, TO, options);
    }
	////////////////////////
	// FECHAS
	public static Date addDays(Date date, int numDiasConSigno) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, numDiasConSigno);
		return cal.getTime();
	}

	public static String getFechaHumana() {
		return getFechaHumana(new Date());
	}
	public static String getFecha_aammdd() {
		return getFecha_aammdd(new Date());
	}
	public static String getFecha_aaaa_mm_dd() {
		return getFecha_aaaa_mm_dd(new Date());
	}
	public static String getHora_HHMMSS() {
		return getHora_HHMMSS(new Date());
	}
	public static String getFechaHumana(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getTime().toString();
	}
	public static String getFecha_aammdd(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String aaaa = "" + cal.get(Calendar.YEAR);
		String mm = padLeftCeros(cal.get(Calendar.MONTH)+1,2);
		String dd = padLeftCeros(cal.get(Calendar.DAY_OF_MONTH),2);
		return aaaa.substring(2) + mm + dd;
	}
	public static String getFecha_aaaa_mm_dd(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String aaaa = "" + cal.get(Calendar.YEAR);
		String mm = padLeftCeros(cal.get(Calendar.MONTH),2);
		String dd = padLeftCeros(cal.get(Calendar.DAY_OF_MONTH),2);
		return aaaa + "-" + mm + "-" + dd;
	}
	public static String getHora_HHMMSS(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String hh = padLeftCeros(cal.get(Calendar.HOUR_OF_DAY),2);
		String mm = padLeftCeros(cal.get(Calendar.MINUTE),2);
		String ss = padLeftCeros(cal.get(Calendar.SECOND),2);
		return hh + mm + ss;
	}
	public static long   getDateInMills() {
		return Calendar.getInstance().getTimeInMillis();
	}
	public static String getHora_HHMMSSDDD(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String hh  = padLeftCeros(cal.get(Calendar.HOUR_OF_DAY),2);
		String mm  = padLeftCeros(cal.get(Calendar.MINUTE),2);
		String ss  = padLeftCeros(cal.get(Calendar.SECOND),2);
		String ddd = padLeftCeros(cal.get(Calendar.MILLISECOND),3);
		return hh + mm + ss + ddd;
	}
	public static String getDateAuditoria() {
		// aammddhhMMssddd
		return getDateAuditoria(new Date());
	}
	public static String getDateAuditoria(Date date) {
		// aammddhhMMssddd
		return getFecha_aammdd(date) + getHora_HHMMSSDDD(date);
	}
	
	public static String cvtFec_dd_mm_aa__saammdd(String dd_mm_aa) {
		String res = dd_mm_aa;
		if ( res != null && res.trim().length() == 8 ) {
			res = res.trim();
			res = "1" + res.substring(6) + res.substring(3,5) + res.substring(0,2);
		}
		return res;
	}
	public static String cvtFec_dd_mm_aa__aammdd(String dd_mm_aa) {
		String res = dd_mm_aa;
		if ( res != null && res.trim().length() == 8 ) {
			res = res.trim();
			res = res.substring(6) + res.substring(3,5) + res.substring(0,2);
		}
		return res;
	}
	public static String cvtFec_saammdd__dd_mm_aa(String saammdd) {
		String res = saammdd;
		if ( res != null && res.trim().length() == 7 ) {
			res = res.trim();
			res = res.substring(5) + "/" + res.substring(3,5) + "/" + res.substring(1,3);
		}
		return res;
	}
	public static String cvtFec_dd_mm_aa__aaaa_mm_dd(String dd_mm_aa) {
		String res = dd_mm_aa;
		if ( res != null && res.trim().length() == 8 ) {
			res = res.trim();
			res = "20" + res.substring(6) + "-" + res.substring(3,5) + "-" + res.substring(0,2);
		}
		return res;
	}
	public static String cvtFec_aaaa_mm_dd__dd_mm_aa(String aaaa_mm_dd) {
		String res = aaaa_mm_dd;
		if ( res != null && res.trim().length() == 10 ) {
			res = res.trim();
			res = res.substring(8) + "/" + res.substring(5,7) + "/" + res.substring(2,4);
		}
		return res;
	}
	////////////////////////
	public static String transformar_Lista_Vertical_a_Horizontal(BeanParaTablaHash[] lista) {
		String enumeracionHorizontal = "";
		if ( lista != null ) {
			for( int i=0; i <lista.length; i++ ) {
				enumeracionHorizontal += i>0?",":"";
				enumeracionHorizontal += lista[i].getValue();
			}
		}
		return enumeracionHorizontal;
	}
//	public static synchronized void addLogSynchronized( BDConexion dataBase, String usr, String t1, String t2, LgAccesoBaseDatos dbLG ) {
//		try {
//			LgBean[] rgsLG = dbLG.lg_getSeq(dataBase, new ConfigPantalla( 1 ), new com.cp.lg_Logs.bean.LgBeanFiltro());	// Debe ser order DESC...
//			if (rgsLG != null) {
//				LgBean rgLG = new LgBean();
//				if (rgsLG.length > 0) {
//					rgLG.setLg_Linea(1L + rgsLG[0].getLg_Linea());
//				} else {
//					rgLG.setLg_Linea(1L);
//				}
//				rgLG.setLg_FechaHora( getFecha_aaaa_mm_dd() + " " + getHora_HHMMSS());
//				rgLG.setLg_crtAutor(usr);
//				rgLG.setLg_Texto_1(t1);
//				rgLG.setLg_Texto_2( Subrutinas.getComputername() + " " + t2);
//			
//				dbLG.lg_crtObj( dataBase, rgLG );
//				
//			}
//		} catch (StExcepcion e) {
//			System.err.println( e.getCause() + "\n" +  e.getMessage() );
//		}
//	}
	public static void addLog( BDConexion dataBase, String usr, String t1, String t2) {
		
		if ( dataBase == null ) { return; }
		
		if ( t1 != null && t1.trim().length() > 500) { t1 = t1.substring(0,500); }
		if ( t2 != null && t2.trim().length() > 500) { t2 = t2.substring(0,500); }

//		addLogSynchronized(dataBase,usr,t1,t2,new com.cp.lg_Logs.db.LgAccesoBaseDatos());
		
	}
	////////////////////////
	public String getDBValueFromKey(BDConexion dbConn, String key) {
		String resultado = null;
		///////////////////////////////
		//	if ( key == null || key.trim().length() < 1 || dbConn == null ) return resultado;
		//	com.tm.nveq_ValoresServidor.db.NveqAccesoBaseDatos db = new com.tm.nveq_ValoresServidor.db.NveqAccesoBaseDatos();
		//	com.tm.nveq_ValoresServidor.bean.NveqBean          rg = new com.tm.nveq_ValoresServidor.bean.NveqBean();
		//	rg.setNveq_EQF4CX(key);
		//	try {
		//	    rg = db.nveq_getRcd(dbConn,rg);
		//	    if ( rg != null ) {
		//		resultado = rg.getNveq_EQSRTY(); // Value
		//	    }
		//	} catch (StExcepcion ex) {;
		//	} finally {
		//	    db = null;
		//	    rg = null;
		//	}
		///////////////////////////////
		return resultado;
	}
	///////////////////////
	
	public byte[] base64_decode( String texto_B64 ) {
		byte[] byteArray = null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byteArray = decoder.decodeBuffer(texto_B64);
		} catch (IOException e) { System.out.println( "base64_decode() : " + e.getMessage() ); }
		return byteArray;
	}
	public String base64_encode( byte[] datos ) {
		String resultado = null;
		if ( datos != null ) {
			resultado = new BASE64Encoder().encode(datos);
		}
        return resultado;
	}

	public BeanParaTablaHash[] cargarLista(HttpServletRequest request, ActionForm form, String idLista, String entidad, String action) {
		BeanParaTablaHash[] lista = null;
		if (lista == null) {
			lista = new BeanParaTablaHash[1];
		}
		lista[0] = new BeanParaTablaHash("", "Sin datos para el usuario");
		if(idLista!=null && idLista.trim().length()>0){
			//            if ("Activo".equalsIgnoreCase(idLista))
			//                return this.cargarLista_Activo(request, form, entidad, action);
			return lista;
		} else {
			return lista;
		}

	}

	public static String sql_OperandoLista(BeanParaTablaHash[] rstLista, String campo, String sqlWhere) {
		String resultado = sqlWhere;
		////////////////////////////////////////
		if (rstLista != null) {
			String operando = "";
			boolean primeraVez = true;
			for (int x = 0; x < rstLista.length; x++) {
				if (rstLista[x].getKey() != null && rstLista[x].getKey().trim().length() > 0) {
					if (!primeraVez) {
						operando += ",";
					}
					operando += "'" + rstLista[x].getKey().trim() + "'";
					primeraVez = false;
				}
			}
			operando = (operando.trim().length() < 1) ? "'nada.'" : operando;

			resultado += (resultado.trim().length() == 0) ? " WHERE " : " AND ";
			resultado += campo + " IN(" + operando + ")";
		}
		////////////////////////////////////////
		return resultado;
	}
	public static String sql_OperandoLista_Not(BeanParaTablaHash[] rstLista, String campo, String sqlWhere) {
		String resultado = sqlWhere;
		////////////////////////////////////////
		if (rstLista != null) {
			String operando = "";
			boolean primeraVez = true;
			for (int x = 0; x < rstLista.length; x++) {
				if (rstLista[x].getKey() != null && rstLista[x].getKey().trim().length() > 0) {
					if (!primeraVez) {
						operando += ",";
					}
					operando += "'" + rstLista[x].getKey().trim() + "'";
					primeraVez = false;
				}
			}
			operando = (operando.trim().length() < 1) ? "'nada.'" : operando;

			resultado += (resultado.trim().length() == 0) ? " WHERE " : " AND ";
			resultado += campo + " NOT IN(" + operando + ")";
		}
		////////////////////////////////////////
		return resultado;
	}

	//////////////////////
	// Ayudas para usar AngularJS
	public static boolean isVersionAngular(HttpServletRequest request,ActionForm form) {
		return Subrutinas.ActionFormfromJson(form,Subrutinas.getRequestPayload_json(request, true));
	}
	public static JSONObject getRequestPayload_json(HttpServletRequest request, boolean isOption_FormDataConvert) {
		/////////////////////////
		JSONObject json = null;
		String request_payload = getRequestPayload_txt(request);
	    if ( request_payload != null && request_payload.trim().length() > 0 ) {
	    	try { json = JSONObject.fromObject( request_payload ); } catch (Exception e) {;}
	    	
	    	////////////
	    	// Si se quiere forzar a json un payload recibido desde un formulario "que no viene en json":
	    	if ( json == null && isOption_FormDataConvert ) {
	    		String trozos[] = request_payload.split("&");
	    		if ( trozos != null && trozos.length > 0 ) {
	    			json = new JSONObject();
	    			for ( String item : trozos ) {
	    				String[] variable = item.split("=");
	    				if ( variable != null && variable.length == 2 ) {
	    					json.put(variable[0], variable[1]);
	    				}
	    			}
	    		}
	    	}
	    	////////////
	    	
	    }
	    return json;
		/////////////////////////
	}
	public static String     getRequestPayload_txt(HttpServletRequest request) {
		/////////////////////////
	    BufferedReader reader = null;
	    StringBuilder buffer = null;
		String request_payload = null;
		try {
			// payloadRequest: Read from request
		    reader = request.getReader();
		    buffer = new StringBuilder();
			while ((request_payload = reader.readLine()) != null) { buffer.append(request_payload); }
			request_payload = buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    buffer = null;
		}
	    return request_payload;
		/////////////////////////
	}
	public static boolean ActionFormfromJson( ActionForm form, JSONObject json ) { 
		boolean resultado = false;

		if ( form == null) return resultado;
		if ( json == null) return resultado;
		
		// Procesa SOLO los campos "public" !!!
		
		/////////
		// Para averiguar el xxBean de que se trata se usa un truco feo deduciéndolo de xxBeanFiltro (Atención a la nomenclatura...mala práctica!!)
		Object beanClassEntidad = null;
		for ( Field formFld : form.getClass().getFields() ) {
			if ( formFld.getType().getName().endsWith("BeanFiltro") ) {
				try {
					String beanClassName = formFld.getType().getName().replace("Filtro","");
					Class<?> clazz = Class.forName( beanClassName );
					Constructor<?> ctor = clazz.getConstructor();
					beanClassEntidad = ctor.newInstance();
					break;
				} 
				catch (ClassNotFoundException e) {break;} catch (NoSuchMethodException e) {break;} 
				catch (SecurityException e) {break;} catch (InstantiationException e) {break;} 
				catch (IllegalAccessException e) {break;} catch (IllegalArgumentException e) {break;} 
				catch (InvocationTargetException e) {break;}
			}
		}
		/////////

		Object nombreFormFld = null;
		for ( Field formFld : form.getClass().getFields() ) {
			try {
				nombreFormFld = formFld.getName();
				
				// Se evita porque este campo es "static". No se debe intentar porque arroja una exception.
				if ( "serialVersionUID".equalsIgnoreCase((String) nombreFormFld) ) continue;
				
				if ( nombreFormFld != null ) {
					Object jsonMember = json.get( nombreFormFld.toString() );
					if ( jsonMember != null ) {
						try {
							if ( "java.lang.String".equalsIgnoreCase( formFld.getType().getName() ) ) {
								formFld.set( form, jsonMember.toString() );
							} else 
							if ( "int".equalsIgnoreCase( formFld.getType().getName() ) ) {
								formFld.set( form, Subrutinas.parse_integer( jsonMember.toString() ) );
							} else 
							if ( "long".equalsIgnoreCase( formFld.getType().getName() ) ) {
								formFld.set( form, Subrutinas.parse_long( jsonMember.toString() ) );
							} else 
							if ( "double".equalsIgnoreCase( formFld.getType().getName() ) ) {
								formFld.set( form, Subrutinas.parse_double( jsonMember.toString() ) );
							} else
							if (jsonMember instanceof JSONObject) {
								// Por ejemplo el Filtro que es un bean que extiende de StBean, con el toString() sobreescrito para generar un json...
								if ( formFld.getType().getName().endsWith("BeanFiltro") ) {
									Class<?> clazz = Class.forName( formFld.getType().getName() );
									if ( null != clazz ) {
										Constructor<?> ctor = clazz.getConstructor();
										if ( null != ctor ) {
											Object object = ctor.newInstance();
											if ( null != object ) {
												Object cosa = JSONObject.toBean( (JSONObject)jsonMember, object.getClass() );
												formFld.set( form, cosa );
											}
										}
									}
								}
							} else 
							if (jsonMember instanceof JSONArray) {
								// Pueden ser varios: "clavesMarcadas", "grid", etc...
								if ( formFld.getName().equalsIgnoreCase("clavesMarcadas") ) {
									try { 
										// public String[] clavesMarcadas; // Para acciones colectivas de una lista....
										JSONArray jsonArray = JSONArray.fromObject(jsonMember);
										String[] clavesMarcadas = new String[jsonArray.size()];
										for ( int i=0; i < jsonArray.size(); i++ ) {
											if ( JSONNull.getInstance() != jsonArray.get(i) ) {
												clavesMarcadas[i] = (String) jsonArray.get(i);
											}
										}
										formFld.set( form, clavesMarcadas );
									} catch (Exception e) { e.printStackTrace(); }
								} else 
								if ( formFld.getName().equalsIgnoreCase("grid") ) {
									if ( beanClassEntidad != null ) {
										try { 
											// public StBean[] grid;
											JSONArray jsonArray = JSONArray.fromObject(jsonMember);
											StBean[] grid = new StBean[jsonArray.size()];
											for ( int i=0; i < jsonArray.size(); i++ ) {
												if ( JSONNull.getInstance() != jsonArray.get(i) ) {

													JSONObject un_bean = (JSONObject)jsonArray.get(i);

													grid[i] = (StBean) JSONObject.toBean( un_bean, beanClassEntidad.getClass() );
												}
											}
											formFld.set( form, grid ); 
										} catch (Exception e) { e.printStackTrace(); }
									}
								}
							}

							resultado = true;

						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
					}
				}
			} catch (SecurityException e) {
//					e.printStackTrace();
			} catch (IllegalArgumentException e) {
//					e.printStackTrace();
//			} catch (IllegalAccessException e) {
//					e.printStackTrace();
			}
		}
		return resultado;
	}
	public static boolean ActionFormToJson( ActionForm form, JSONObject json ) { 
		boolean resultado = false;

		if ( form == null) return resultado;
		if ( json == null) return resultado;
		
		// Procesa SOLO los campos "public" !!!

		Object valor_N = null;
		Object valor_V = null;
    	for ( Field f : form.getClass().getFields() ) {
			try {
				valor_N = f.getName();
				valor_V = f.get( form );
			} catch (SecurityException e) {
//				e.printStackTrace();
			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
			} catch (IllegalAccessException e) {
//				e.printStackTrace();
			}
			if ( valor_N != null && valor_V != null ) {
//				// Con .toString no traducía a texto el grid, sino que sacaba el puntero del objeto: [Lcom.fvr.us_Usuarios.bean.UsBean;@6c71f5
//				json.put(valor_N.toString(), valor_V.toString());
				json.put(valor_N.toString(), valor_V);
				if (!resultado) { resultado = true; }
			}
    	}

		return resultado;
	}
	public static void returnActionVersionAngular( HttpServletRequest request, HttpServletResponse response, Object laClaseInformante_o_null, boolean isOk, String texto_SiEsOK ) throws IOException {
		String textoDeSalida = "";
		///////////////
		if ( isOk ) {
			textoDeSalida = texto_SiEsOK;
		} else {
			ActionMessages errores = (ActionMessages) request.getAttribute( "org.apache.struts.action.ERROR" );
			if ( errores != null ) {
				@SuppressWarnings("unchecked")
				Iterator<ActionMessage> it = errores.get();
	    		ActionMessage item = null;
	        	while ( it.hasNext() ) {
	        		item = it.next();
	        		if ( item != null ) {
	        			for( Object frase : item.getValues() ) {
	        				textoDeSalida += frase + "\n";
	        			}
	        		}
	        	}
			}
		}
		///////////////
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();

		json.put( "server", Subrutinas.getComputername() );
		json.put( "class",  null!=laClaseInformante_o_null?laClaseInformante_o_null.getClass().getSimpleName():"" );
		json.put( "rc",     isOk?"OK":"KO" );
		json.put( "text",   textoDeSalida );

		out.print( json.toString() );

		out.close();
		///////////////
    }
	//////////////////////

}
