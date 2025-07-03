package tn.esprit.pfe.approbation.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import tn.esprit.pfe.approbation.dtos.TaskDetailsDto;
import tn.esprit.pfe.approbation.entities.LeaveRequest;
import tn.esprit.pfe.approbation.repositories.LeaveRequestRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReportService {

    private final HistoryService historyService;
    private final LeaveRequestRepository leaveRequestRepository;

    private JasperReport compileReport(InputStream inputStream) throws JRException {
        log.info("Compiling Jasper Report");
        if (inputStream == null) throw new JRException("input stream is null");
        return JasperCompileManager.compileReport(inputStream);
    }

    public byte[] generateReport(String templatePath, Map<String, Object> params, JRBeanCollectionDataSource dataSource) throws JRException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("reports/" + templatePath + ".jrxml");
        if (inputStream == null) {
            log.error("Template file not found: /reports/{}.jrxml", templatePath);
            throw new JRException("Template file not found: /reports/" + templatePath + ".jrxml");
        }
        try (inputStream) {
            JasperReport report = compileReport(inputStream);
            JasperPrint print = JasperFillManager.fillReport(report, params, new JREmptyDataSource());
            final Exporter exporter = new JRPdfExporter();
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.exportReport();
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error while processing the report template", e);
            throw new JRException("Error while processing the report template", e);
        }
    }

    public byte[] generateAvisCongeReport(String instanceId) throws JRException {
        LeaveRequest leaveRequest = leaveRequestRepository.findByProcInstId(instanceId);
        if (leaveRequest != null) {
            String templatePath = "avis_conge_report";
            List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(instanceId)
                    .list();
            List<TaskDetailsDto> taskDetails = tasks.stream()
                    .map(task -> new TaskDetailsDto(
                            task.getId(),
                            task.getRootProcessInstanceId(),
                            task.getName(),
                            task.getAssignee(),
                            task.getOwner(),
                            task.getStartTime(),
                            task.getEndTime(),
                            task.getDescription(),
                            task.getDeleteReason(),
                            null
                    ))
                    .collect(Collectors.toList());
            long daysRequested = ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate())+1;
            log.info("Days requested: " + daysRequested);
            String jour="";
            if (daysRequested<=1) {
                jour="jour";
            }
            else if (daysRequested>1) {
                jour="jours";
            }
            String ManagerName = leaveRequest.getUser().getManager().getFirstName() + " " + leaveRequest.getUser().getManager().getLastName();
            String user = leaveRequest.getUser().getFirstName() + " " + leaveRequest.getUser().getLastName();
            String allText = "Je, soussigné(e) Mr/Mme " + ManagerName + " autorise Mr/Mme " + user + " à prendre un congé du " + leaveRequest.getStartDate() + " au " + leaveRequest.getEndDate() + " " +
                   " pour un total de "+daysRequested+" "+jour+"." ;
            log.info("Generating Avis Conge Report" + ManagerName);
            Map<String, Object> reportParams = new HashMap<>();
            reportParams.put("LeaveRequestId", leaveRequest.getId());
            reportParams.put("EmployeeName", user);
            reportParams.put("ManagerName", ManagerName);
            reportParams.put("LeaveStartDate", leaveRequest.getStartDate());
            reportParams.put("LeaveEndDate", leaveRequest.getEndDate());
            reportParams.put("taskDate",taskDetails.get(0).getEndTime().toString());
            reportParams.put("allText", allText);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(taskDetails);
            System.out.println(reportParams);
            return generateReport(templatePath, reportParams, dataSource);
        } else {
            throw new IllegalArgumentException("LeaveRequest not found");
        }

    }

}

