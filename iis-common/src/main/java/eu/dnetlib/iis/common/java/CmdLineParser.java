package eu.dnetlib.iis.common.java;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Mateusz Kobos
 *
 */
public class CmdLineParser {
	/** HACK: make the names of various types of parameters of the program 
	* more readable, e.g. "--Input_person=..." instead of "-Iperson=...",
	* "--Output_merged=..." instead of "-Omerged=...". I wasn't able to
	* get such notation so far using the Apache CLI. */
	public static String constructorPrefix = "C";
	public static String inputPrefix = "I";
	public static String outputPrefix = "O";
	public static String specialParametersPrefix = "S";
	/** HACK: This field should be removed since this list of special 
	 * parameters is empty, thus not used anywhere.*/
	public static String[] mandatorySpecialParameters = 
			new String[]{};
	public static String processParametersPrefix = "P";
	
	private CmdLineParser() {}
	
	public static CommandLine parse(String[] args) {
		
        Option constructorParams = Option.builder(constructorPrefix).argName("STRING").hasArg()
                .desc("Constructor parameter").longOpt("ConstructorParam").build();
        
        Option inputs = Option.builder(inputPrefix).argName("portName=URI").numberOfArgs(2).valueSeparator()
                .desc("Path binding for a given input port").longOpt("Input").build();

        Option outputs = Option.builder(outputPrefix).argName("portName=URI").numberOfArgs(2).valueSeparator()
                .desc("Path binding for a given output port").longOpt("Output").build();
        
        Option specialParameter = Option.builder(specialParametersPrefix).argName("parameter_name=string").numberOfArgs(2).valueSeparator()
                .desc(String.format("Value of special parameter. "
                        + "These are the mandatory parameters={%s}",
                        StringUtils.join(mandatorySpecialParameters, ","))).longOpt("SpecialParam").build();
		
        Option otherParameter = Option.builder(processParametersPrefix).argName("parameter_name=string").numberOfArgs(2).valueSeparator()
                .desc("Value of some other parameter.").longOpt("ProcessParam").build();
		
		Option help = new Option("help", "print this message");

        Options options = new Options();
        options.addOption(constructorParams);
        options.addOption(inputs);
        options.addOption(outputs);
        options.addOption(specialParameter);
        options.addOption(otherParameter);
        options.addOption(help);
        
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmdLine = parser.parse(options, args);
			if(cmdLine.hasOption("help")){
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("", options );
				System.exit(1);
			}
			return cmdLine;
		} catch (ParseException e) {
			throw new CmdLineParserException(
					"Parsing command line arguments failed", e);
		}
		
	}
}
