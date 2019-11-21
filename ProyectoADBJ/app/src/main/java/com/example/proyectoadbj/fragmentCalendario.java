package com.example.proyectoadbj;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentCalendario extends Fragment {


    // Fields:

    String fullDate;
    private String activeUserName;
    ArrayList<workoutEvent> workoutEventList;

    // Data access object y querydump
    DAO dao;
    queryDump q=new queryDump();

    // Controles
    ListView lvCalendario;
    Usuario usuario;


    public fragmentCalendario() {
        // Required empty public constructor
    }

    // Constructor para crear instancias de este fragmento
    public static fragmentCalendario crearFragmentClases(String activeUserName){
        fragmentCalendario frag=new fragmentCalendario();
        // Crear un bundle de argumentos para inyectar en el fragmento
        Bundle argumentos=new Bundle();
        // Acá se pueden insertar tantos argumentos como sea necesario en pares key-value
        argumentos.putString("activeUserName",activeUserName);
        // Registrar como argumentos.
        frag.setArguments(argumentos);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Desempacar bundle y extraer nombre de usuario activo.
        activeUserName=getArguments().getString("activeUserName");
        System.out.println("OnCreate Calendario activeUserName: "+activeUserName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_tab_calendario, container, false);
        //Views y controles
        lvCalendario=view.findViewById(R.id.lvCalendario);
        // Inicializar fragmento
        initCalendarioFragment(container.getContext());
        return view;
    }

    public void initCalendarioFragment(Context context){

        // Creacion del objeto de acceso a BD
        dao = new DAO(context);

        // Obtener id del usuario
        int idUsuario=dao.getIdFromUsuario(activeUserName);
        // Llenar lista

        // Crear un array persistente con los objetos workout del listview
        workoutEventList=dao.getCalendarEvents(idUsuario);

        // Convertir el workoutEventList en una lista de strings.
        ArrayList<String> workoutStringList=new ArrayList<>();

        for (workoutEvent we : workoutEventList)
        {
            workoutStringList.add(we.workoutDescription());
        }

        UIHelpers.fillListView(lvCalendario,workoutStringList,context);

        // Eliminar registro



        lvCalendario.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener workout seleccionado
                workoutEvent event=workoutEventList.get(position);
                boolean exito=dao.exitEvent(dao.getIdFromUsuario(activeUserName),event.getId());
                if (exito){
                    // Eliminado correctamente, eliminar tambien de caché local
                    workoutEventList.remove(position);
                    errorHandler.Toaster(enumErrores.eliminadoCorrectamente,view.getContext());
                }else{
                    errorHandler.Toaster(enumErrores.noSePuedeEliminar,view.getContext());
                }
                // Redibujar calendario
                initCalendarioFragment(view.getContext());

                return true;
            }
        });

    }

}
