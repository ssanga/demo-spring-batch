package com.example.demospringbatch;

import com.example.demospringbatch.listener.JobListener;
import com.example.demospringbatch.model.Movie;
import com.example.demospringbatch.model.Persona;
import com.example.demospringbatch.processor.PersonaItemProcessor;
import com.example.demospringbatch.services.MoviesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private static final Logger LOGGER= LoggerFactory.getLogger(BatchConfiguration.class);

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MoviesService moviesService;

    @Bean
    public FlatFileItemReader<Persona> reader(){
        return new FlatFileItemReaderBuilder<Persona>()
                .name("personaItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[] {"primerNombre","segundoNombre","telefono"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Persona>() {{
                    setTargetType(Persona.class);
                }})
                .build();
    }

    @Bean
    public PersonaItemProcessor processor(){
        return new PersonaItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Persona> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Persona>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO persona (primer_nombre, segundo_nombre, telefono) VALUES (:primerNombre, :segundoNombre, :telefono)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importPersonaJob(JobListener listener, Step step){

        var movies = moviesService.getMovies();

        for (Movie m:movies) {
            LOGGER.info(m.getName());
        }

        return jobBuilderFactory.get("importPersonaJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step)
                .end()
                .build();
    }

    @Bean
    public Step step(JdbcBatchItemWriter<Persona> writer){
        return stepBuilderFactory.get("step")
                .<Persona,Persona> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }
}
