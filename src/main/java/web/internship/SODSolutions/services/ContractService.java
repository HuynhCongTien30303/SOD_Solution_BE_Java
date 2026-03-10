package web.internship.SODSolutions.services;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.internship.SODSolutions.dto.request.ReqContractDTO;
import web.internship.SODSolutions.dto.response.ResContractDTO;
import web.internship.SODSolutions.mapper.ContractMapper;
import web.internship.SODSolutions.model.Contract;
import web.internship.SODSolutions.model.Project;
import web.internship.SODSolutions.repository.ContractRepository;
import web.internship.SODSolutions.repository.ProjectRepository;
import web.internship.SODSolutions.repository.UserRepository;
import web.internship.SODSolutions.util.SecurityUtil;
import web.internship.SODSolutions.util.error.AppException;

import java.util.List;


@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ContractService {
    ContractRepository contractRepository;
    ContractMapper contractMapper;
    UserRepository userRepository;
    ProjectRepository projectRepository;

    public List<ResContractDTO> getAllContracts() {
        return contractMapper.toResContractDTOs(contractRepository.findAll());
    }

    public List<ResContractDTO> getContractsByEmail(){
        String currentUserEmail = SecurityUtil.getCurrentUserLogin().orElse(null);

        if (!userRepository.existsByEmail(currentUserEmail)) {
            throw new AppException("User not found with email: " +currentUserEmail);
        }

        return contractMapper.toResContractDTOs(contractRepository.getContractsByProject_User_Email(currentUserEmail));
    }

    public List<ResContractDTO> getContractByProjectId(long projectId) {
        if(!projectRepository.existsById(projectId)) {
            throw new AppException("Project not found with id: " + projectId);
        }

        return contractMapper.toResContractDTOs(contractRepository.getContractsByProject_Id(projectId));
    }

    @Transactional
    public ResContractDTO createContract(ReqContractDTO reqContractDTO) {
        Project project = projectRepository.findById(reqContractDTO.getProjectId())
                .orElseThrow(()-> new AppException("Project not found with id: " + reqContractDTO.getProjectId()));

        Contract contract = contractMapper.toContract(reqContractDTO);
        contract.setProject(project);
        contractRepository.save(contract);

        return contractMapper.toResContractDTO(contract);
    }

    @Transactional
    public ResContractDTO updateContract(ReqContractDTO reqContractDTO) {
        Contract existingContract = contractRepository.findById(reqContractDTO.getId())
                .orElseThrow(()-> new AppException("Contract not found with id: " + reqContractDTO.getId()));

        Project project = projectRepository.findById(reqContractDTO.getProjectId())
                .orElseThrow(()-> new AppException("Project not found with id: " + reqContractDTO.getProjectId()));

        existingContract.setProject(project);
        existingContract.setTotalAmount(reqContractDTO.getTotalAmount());
        existingContract.setSignedDate(reqContractDTO.getSignedDate());
        existingContract.setContractNumber(reqContractDTO.getContractNumber());
        existingContract.setContractFile(reqContractDTO.getContractFile());

        contractRepository.save(existingContract);
        return contractMapper.toResContractDTO(existingContract);
    }

    @Transactional
    public void deleteContract(long id) {
        if(!contractRepository.existsById(id)) {
            throw new AppException("Contract not found with id: " + id);
        }

        contractRepository.deleteById(id);
    }

}
