package com.cp._comun;

import java.io.File;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.cp.FuentesDeDatos.BDConexion;

/**
 * @author Emilio Estecha 2013
 *
 */
public class Upload {
    public Upload() {}
    public void proceso(HttpServletRequest request, HttpServletResponse response, String dir) {
        
        String filename = null;
        String area = null;
        String key_prefix = null;
        String tipoDoc = null;
        
        BDConexion dataBase =  new Subrutinas().getBDConexion(request);
        
        if (ServletFileUpload.isMultipartContent(request)){
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                Iterator<FileItem> iter = null;
                @SuppressWarnings("unchecked")
				java.util.List<FileItem> items = upload.parseRequest(request);
                // Primero busco el nombre de archivo destino:
                iter = items.iterator();
                while ( iter.hasNext() ) {
                    FileItem item = iter.next();
                    if (item.isFormField() ) {
                    	
                    	// Campos del formulario:
                    	if ( "area".equalsIgnoreCase( item.getFieldName() ) ) 		{area = item.getString();}
                    	if ( "key_prefix".equalsIgnoreCase( item.getFieldName() ) ) {key_prefix = item.getString();}
                    	if ( "tipoDoc".equalsIgnoreCase( item.getFieldName() ) ) 	{tipoDoc = item.getString();}

                    }
                }
                
                // Ahora busco el "archivo" en si mismo:
                iter = items.iterator();
                while ( iter.hasNext() ) {
                    FileItem item = iter.next();
                    if ( !item.isFormField() ) {

                    	filename = item.getName();
                        int idx = filename.lastIndexOf(File.separator);
                        if (idx>-1) filename = filename.substring(idx+1);	// Corrección para IE

                    	filename = key_prefix + " " + filename;

                        processUploadedFile( item, dir + File.separator + area, filename );
                        break;
                    }
                }

                // Doy de alta el anexo en AX (ANEXOS) y en CAAX (Anexos de capitalizaciones)
                if ( generarAnexo(dataBase, area, tipoDoc, key_prefix, filename) ) {
                    System.out.println("*** Subido y anexado el fichero: " + filename);
                };


            } catch (Exception ex) {
                ex.printStackTrace();
            }
            

        }
    }

    private void processUploadedFile(FileItem item, String dir, String nombreExterno) throws Exception {
        String nombre = item.getName();
        
        if ( nombreExterno != null && nombreExterno.trim().length() > 0 )
            nombre = nombreExterno;
        
        new File(dir).mkdirs();
        
        File archivoDestino = new File(dir + File.separator + nombre );
        item.write( archivoDestino );
    }

    private boolean generarAnexo(BDConexion dataBase, String area, String tipoDoc, String key, String filename) throws Exception {
    	
    	boolean resultado = false;

//    	// Recupero el tipo de documento:
//    	com.cp.pa_parametros.db.PaAccesoBaseDatos 	dao_pa = new com.cp.pa_parametros.db.PaAccesoBaseDatos();
//    	com.cp.pa_parametros.bean.PaBean			reg_pa = new com.cp.pa_parametros.bean.PaBean();
//       	try {
//        	// PK:
//        	reg_pa.setKey( tipoDoc );	// Utililza el separador de campos "^"...
//       		reg_pa = dao_pa.pa_getRcd(dataBase, reg_pa);
//		} catch (StExcepcion e) {
//				e.printStackTrace();
//    	}
//    	
//       	if ( reg_pa == null ) {
//       		throw new Exception("ERROR AL RECUPERAR EL TIPO DE DOCUMENTO EN PA");
//       	}
//       	
//		// ANEXOS:
//    	com.cp.ax_anexos.db.AxAccesoBaseDatos	dao_ax = new com.cp.ax_anexos.db.AxAccesoBaseDatos();
//    	com.cp.ax_anexos.bean.AxBean			reg_ax = new com.cp.ax_anexos.bean.AxBean();
//    	// PK:
//    	reg_ax.setAx_id_anexo("AX-" + key + "-" + tipoDoc);	 // id_anexo
//    	// Resto:
//    	reg_ax.setAx_nombre( reg_pa.getPa_value() ); // nombre
//    	reg_ax.setAx_comentario(""); // comentario
//    	reg_ax.setAx_fec_alta( Subrutinas.getFecha_aaaa_mm_dd() ); // fec_alta
//    	reg_ax.setAx_estado("Registrado"); // estado
//    	reg_ax.setAx_indicador(""); // indicador
//    	reg_ax.setAx_id_entidad( area ); // id_entidad
//    	reg_ax.setAx_id_clave( key ); // id_clave
//    	reg_ax.setAx_filename( filename ); // filename
//    	reg_ax.setAx_json(""); // json
//
//    	try {
//			dao_ax.ax_dltObj(dataBase, reg_ax);
//			dao_ax.ax_crtObj(dataBase, reg_ax);
//
//			// ANEXOS DE CAPITALIZACIONES:
//	    	com.cp.caax_anexoscapitalizaciones.db.CaaxAccesoBaseDatos	dao_caax = new com.cp.caax_anexoscapitalizaciones.db.CaaxAccesoBaseDatos();
//	    	com.cp.caax_anexoscapitalizaciones.bean.CaaxBean			reg_caax = new com.cp.caax_anexoscapitalizaciones.bean.CaaxBean();
//	    	// PK:
//	    	reg_caax.setCaax_id_capitalizacion( key ); // id_capitalizacion
//	    	reg_caax.setCaax_id_anexo( reg_ax.getAx_id_anexo() ); // id_anexo
//	    	// Resto:
//	    	reg_caax.setCaax_json(""); // json
//
//	    	dao_caax.caax_dltObj(dataBase, reg_caax);
//	    	dao_caax.caax_crtObj(dataBase, reg_caax);
//	    	
//	    	resultado = true;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return resultado;

	}

}
