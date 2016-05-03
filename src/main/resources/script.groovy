import groovy.json.*

class GroovyProcessor implements Processor {

	def locations= ["37.775,-122.4183333", "37.4419444,-122.1419444", "44.7694,-106.969", "32.7152778,-117.1563889", "43.479929,-110.762428", "41.850033,-87.6500523", "42.3584308,-71.0597732", "34.0522342,-118.2436849", "41.9015141,12.4607737", "51.536086,-0.131836", "55.751849,37.573242", "48.864715,2.329102"]
	
    def REL_SUCCESS = new Relationship.Builder().name("success").description("FlowFiles that were successfully processed").build();
    def ProcessorLog log

    @Override
    void initialize(ProcessorInitializationContext context) {
        log = context.getLogger()
    }

    @Override

    Set<Relationship> getRelationships() {
        return [REL_SUCCESS] as Set
    }

    @Override
    void onTrigger(ProcessContext context, ProcessSessionFactory sessionFactory) throws ProcessException {
        try {

            def session = sessionFactory.createSession()
            def flowFile = session.get()
            if (!flowFile) return
            flowFile = session.write(flowFile,
                    { inputStream, outputStream ->
                        String line
                        final BufferedReader inReader = new BufferedReader(new InputStreamReader(inputStream, 'UTF-8'))
                        line = inReader.readLine()
						if(line== null) return
						def jsonSlurper = new JsonSlurper()
						def json= jsonSlurper.parseText(line)
						if(json.id==null)
							json.id= ""+System.currentTimeMillis()
						if(json.location==null) 
						 	json.location= locations[new Random().nextInt() % 12]
						if(json.event_timestamp==null) {
							json.event_timestamp= new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'")
						}
						outputStream.write(JsonOutput.toJson(json).getBytes())
                    } as StreamCallback)
            // transfer
            session.transfer(flowFile, REL_SUCCESS)
            session.commit()
        }
        catch (e) {
            throw new ProcessException(e)
        }
    }

    @Override
    Collection<ValidationResult> validate(ValidationContext context) { return null }

    @Override
    PropertyDescriptor getPropertyDescriptor(String name) { return null }

    @Override

    void onPropertyModified(PropertyDescriptor descriptor, String oldValue, String newValue) { }

    @Override

    List<PropertyDescriptor> getPropertyDescriptors() { return null }

    @Override

    String getIdentifier() { return null }
}

processor = new GroovyProcessor()
