package com.cwidanage.photoftp.services;

import com.cwidanage.photoftp.models.Fotografi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
/*import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
*/import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.repository.FotografiRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class FotografiService extends AbstractService /*implements UserDetailsService*/{

	@Autowired
	private FotografiRepository fotografiRepository;
	
	public Page<Fotografi> list(int pageNumber, int pageSize) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        return fotografiRepository.findAll(pageRequest);
	}
	
	public Fotografi create(Fotografi fotografi) throws ValidationException {
		return this.fotografiRepository.save(fotografi);
	}
	public  Fotografi getFotografiById(Integer id){
		return  this.fotografiRepository.findOne(id);
	}
	public Boolean delete(Integer id) {

		fotografiRepository.delete(id);
		return true;

	}
	public List<Fotografi> getFotografiByName(String name){
		return fotografiRepository.findFirstByNome_fotografoIgnoreCase(name);
	}
	
/*	@Override
    public UserDetails loadUserByUsername(String username) {
	
		if(StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException(username);
		}
		
		Optional<Fotografi> user = fotografiRepository.findByUsername(username);
        
		if (!user.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
		
        try {
			return new org.springframework.security.core.userdetails.User(user.get().getusername(),
					user.get().getpassword(), Arrays.asList(new SimpleGrantedAuthority(user.get().getAuthorsPosition().toUpperCase())));
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }*/
	
}