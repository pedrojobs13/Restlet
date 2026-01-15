package controller;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class LivroController extends ServerResource {

    @Get("json")
    public String listar(){
        return "{\"mensagem\":\"Listando usuários\"}";
    }

}
