package Services;

import DAL.Contracts.CrudRepository;
import DAL.DataEntities.Registers.Location;
import Services.Dto.LocationDto;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("/locations")
public class LocationsResource {
    @EJB(beanName = "LocationRepo")
    CrudRepository<Location, Long> locationRepo;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/")
    public List<LocationDto> getAllocations(){

        return locationRepo.getAll()
                .stream()
                .map(x->new LocationDto(x.getId(),
                                        x.getOrganization().getFullName(),
                                        x.getLocName(),
                                        x.getLocationCity(),
                                        x.getLocationStreet(),
                                        x.getHouseNumber(),
                                        x.getBuilding(),
                                        x.getApartmentNumber(),
                                        x.getInfo()))
                .collect(Collectors.toList());
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/{id}")
    public LocationDto getLocation(@PathParam("id") Long id){
        Location source = locationRepo.getItem(id);
        LocationDto res = new LocationDto();
        res.setId(source.getId());
        res.setLocName(source.getLocName());
        res.setOrganization(source.getOrganization().getFullName());
        res.setLocCity(source.getLocationCity());
        res.setLocStreet(source.getLocationStreet());
        res.setLocHouseNumber(source.getHouseNumber());
        res.setLocBuilding(source.getBuilding());
        res.setLocApartmentNumber(source.getApartmentNumber());
        res.setLocInfo(source.getInfo());
        return res;
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    public void deleteLocation(@PathParam("id") Long id){
        locationRepo.deleteItem(id);
    }
}
