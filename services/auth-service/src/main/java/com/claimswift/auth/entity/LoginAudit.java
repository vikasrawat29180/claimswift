package com.claimswift.auth.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "login_audit")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class LoginAudit 
{ 
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id; 
	private String username;
	private boolean success; 
	private String message; 
	
	private LocalDateTime timestamp; 
}
