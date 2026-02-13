package com.crudpractica.finanzastestproyec.Model;




/*
 *   Entidad que reprensenta  un cliente  de la entidad financiera
 * un cliente debe ser mayor  de edad para poder registrar en el sistema
 * no puede ser eliminado si tiene productos financieros vinculados
 *
 * */


import com.crudpractica.finanzastestproyec.Enums.TipoIdentifiacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.bridge.IMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Cliente {

    //identificador unico del cliente

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;


        //tipo de documento de identificacion del cliente

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_identificacion", nullable = false, length = 30)
        @NotNull(message = "El tipo de identificacion es obligatorio")

    private TipoIdentifiacion tipoIdentifiacion;




    //Numero de documento  de identicacion  del cliente

    @Column(name = "numero_identificacion", nullable = false, length = 20,  unique = true)
    @NotBlank(message = "El numero de identificacion es Obligatrio")
    @Size(min = 5, max =20 ,message = "El numero de identificacion debe terner entre 5 y 20 caracteres")
    private String numeroIdentificacion;

    /*
    * Nombre del cliente
    * debe tener minimo 2 caracterres
    */


    @Column(name = "nombres", nullable = false, length = 100)
    @NotBlank(message = "los nombres son Obligatorios")
    @Size(min = 2, max = 100, message = "Los Nombres  deben  tener entre 2 y 100 caracteres")
    private String nombres;



    /*
     * Apellidos del cliente
     * debe tener minimo 2 caracterres
     */

    @Column(name = "apellido", nullable = false, length = 100)
    @NotBlank(message = "los nombres son Obligatorios")
    @Size(min = 2, max = 100, message = "Los Nombres  deben  tener entre 2 y 100 caracteres")
    private String apellido;


    /*
    Correo electronico del cliente
    debe tener formato valido(xxxx@xxxx.xxx)

    * */
    @Column(name = "correo_electronico",  nullable = false, unique = true, length = 100)
    @NotBlank(message = "El_correo electronico es obligatorio")
    @Email(message = "El correo electronico debe tener un formato valido")
    private String correoElectronico;

/*

    Fecha de nacimiento del cliente
    el cliente debe ser mayor de edad(18 años)
  */



    @Column(name = "fecha_nacimiento", nullable = false)
    @NotNull(message = "La fecha de nacimiento  es obligatoria")
    @Past(message = "la fecha de nacimiento debe ser anterior a la fecha  actual")
    private LocalDate fechaNacimiento;

    /*

    Fecha de creacion del registro del cliente
    se calcula automaticamente al crear el cliente
    */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /*

    Fecha de la ultima modificacion del cliente
    se actualiza automaticamente al modificar el cliente
    */
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    /*

    Lista de cuentas financieras asociadas al cliente
    */

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cuenta> cuentas;


    /*Metodo ejecutando antes de persistir el cliente
    * establece la fecha de creacion automaticamente
     */

    @PrePersist
    protected void onCreated(){
        this.fechaCreacion = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
    }

    /*
    * Metodo ejecutado antes de actualizar el cliente
    * actualiza la fecha de modificaciones automaticamente
    * */
    @PreUpdate
    protected void onUpdate(){
        this.fechaModificacion = LocalDateTime.now();
    }

    /*
    * Calcula la edad del cliente basandose  en su fecha  de nacimiento
    * @return edad del cliente en años
    *
    * */

    public int calcularEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();

    }
        /*
        * Verifica si el cliente es mayor  de edad(18años o mas)
        * @return true si el cliente es mayor  de edad, false en caso contrario
        *
        * */

        public boolean esMayorDeEdad(){
            return calcularEdad() >= 18;
        }

        /*Verifica  si el cliente tiene cuentas vinculadas
        *
        * @return true si tiene cuentas, false en caso contrario
        * */

    public boolean tieneCuentasVinculadas(){
        return cuentas != null && !cuentas.isEmpty();
    }
}

