En español sólo términos propios del dominio del problema (Locales, Canchas, Usuarios, Invitaciones, etc.)

En inglés todo lo demás (filter, view, get, save, generate, from, etc.)

Backend  
Carpetas: dentro de domain, para cada subdominio (y con sus sufijos):

- Controller (\*Controller.java)  
  - Dto (\*Request.java, \*Response.java, etc.)  
  - Mapper (\*Mapper.java)

- Exception (\*Exception.java)  
- Model   
- Service (\*Service.java)  
- Dto (\*Command.java, \*Result.java, etc)  
- Mapper (\*Mapper.java)  
- Repository (\*Repository.java)

Los controllers usan los DTOs para request, responses y demás formas en las que presentar sus datos, según el diseño de API establecido previamente en base a los requerimientos de consumo por el frontend

Los servicios usan DTOs (command), para recibir todos los datos que necesiten para procesar una operación, y retornar el resultado.

- La idea de eso es no pasar DTO del controller al service, que eso generaria acoplamiento entre ambas. Lo mismo para el objeto que retorne el service

Preferir usar Records para DTOs en lugar de clases siempre que sea posible

Frontend  
…

