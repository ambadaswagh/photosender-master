package com.cwidanage.photoftp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cwidanage.photoftp.exceptions.ValidationException;
import com.cwidanage.photoftp.models.Competition;
import com.cwidanage.photoftp.models.CompetitionModel;
import com.cwidanage.photoftp.models.Squadre;
import com.cwidanage.photoftp.repository.CompetitionRepository;
import com.cwidanage.photoftp.repository.SquadreRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompetitionService extends AbstractService {

	@Autowired
	private CompetitionRepository competitionRepository;
	
	@Autowired
	private SquadreRepository squadreRepository;
	
	/*@Autowired
	private SquadreRepository squadreRepository;*/
	
	public Page<Competition> list(int pageNumber, int pageSize) {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        return competitionRepository.findAll(pageRequest);
	}
	
	
	public Competition create(CompetitionModel competitionModel) throws ValidationException {
		Competition entity=null;
		//update
		if (competitionModel.getid() != null) {
			entity = this.competitionRepository.findOneByid(competitionModel.getid());
			if (entity != null) {
				entity.setNome_it(competitionModel.getNome_it());
				entity.setSubject(competitionModel.getSubject());
				entity.setSubjectabb(competitionModel.getSubjectabb());
				entity.setKeywords(competitionModel.getKeyword());
				entity = this.competitionRepository.save(entity);
				// update squadre list

				if (competitionModel.getTeams() != null) {
					// remove delete entry
					if (entity.getSquadreList() != null) {
						List<Squadre> removeSquadreList = new ArrayList<>();
						for (Squadre squadre : entity.getSquadreList()) {
							if (!competitionModel.getTeams().contains(squadre.getNome_it())) {
								removeSquadreList.add(squadre);
							}
						}
						entity.getSquadreList().removeAll(removeSquadreList);
						entity=this.competitionRepository.save(entity);
					}
				}
			}
		} else {// create
			entity=new Competition();
			entity.setNome_it(competitionModel.getNome_it());
			entity.setSubject(competitionModel.getSubject());
			entity.setSubjectabb(competitionModel.getSubjectabb());
			entity.setKeywords(competitionModel.getKeyword());
			entity = this.competitionRepository.save(entity);
		}
		// add new entry
		if(competitionModel.getTeams()!=null) {
			for (String team : competitionModel.getTeams()) {
				Squadre squadre=squadreRepository.findOneByNomeIt(team);
				
				if(!entity.getSquadreList().contains(squadre)) {
					entity.getSquadreList().add(squadre);
				}
			}	
		}
		
		entity=this.competitionRepository.save(entity);
		
		return entity;
	}
	public  Competition getCompetitionById(Integer id){
		return  this.competitionRepository.findOne(id);
	}
	public Boolean delete(Integer id) {

		competitionRepository.delete(id);
		return true;
	}
	public List<Competition> getCompetitionBySubject(String subject){
		return competitionRepository.findBySubjectIgnoreCase(subject);
	}
}
