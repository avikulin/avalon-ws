package Services;

import DAL.Contracts.CrudRepository;
import DAL.DataEntities.Registers.Equipment;
import Services.Dto.EquipmentDto;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("/equipment")
public class EquipmentResource {
    @EJB(beanName = "EquipmentRepo")
    CrudRepository<Equipment, Long> equipmentRepo;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/")
    public List<EquipmentDto> getAllocations(){
        return equipmentRepo.getAll()
                .stream()
                .map(x -> new EquipmentDto(x.getId(),
                                           x.getCode(),
                                           x.getDescription(),
                                           x.getModel().toString(),
                                            x.getLocation().toString(),
                                            x.getIpAddress()))
                .collect(Collectors.toList());
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/{id}")
    public EquipmentDto getLocation(@PathParam("id") Long id){
        EquipmentDto res = new EquipmentDto();
        Equipment source = equipmentRepo.getItem(id);
        res.setId(source.getId());
        res.setCode(source.getCode());
        res.setDescription(source.getDescription());
        res.setModel(source.getModel().toString());
        res.setLocation(source.getLocation().toString());
        res.setIpAddress(source.getIpAddress());
        return res;
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    public void deleteLocation(@PathParam("id") Long id){
        equipmentRepo.deleteItem(id);
    }
}
