package org.switchyard.component.camel.config.model.file;

/**
 * Represents the configuration settings for a File Producer binding in Camel.
 */
public interface CamelFileProducerBindingModel {

    /**
     * What to do if a file already exists with the same name. The following
     * values can be obtained: Override, Append, Fail and Ignore.
     * 
     * @return the fileExist value
     */
    public String getFileExist();

    /**
     * Specify what to do if a file already exists with the same name.
     * 
     * @param fileExist
     *            one of these: Override, Append, Fail or Ignore
     * @return a reference to this Camel File binding model
     */
    public CamelFileProducerBindingModel setFileExist(String fileExist);

    /**
     * This option is used to write the file using a temporary name and then,
     * after the write is complete, rename it to the real name.
     * 
     * @return the tempPrefix value
     */
    public String getTempPrefix();

    /**
     * Specify the name of the temporary name when writting it. After the write
     * is complete, it will be renamed to the real name.
     * 
     * @param tempPrefix
     *            the temporary file name
     * @return a reference to this Camel File binding model
     */
    public CamelFileProducerBindingModel setTempPrefix(String tempPrefix);

    /**
     * Camel 2.1: The same as tempPrefix option but offering a more fine grained
     * control on the naming of the temporary filename.
     * 
     * @return the temporary file name value
     */
    public String getTempFileName();

    /**
     * Camel 2.1: The same as tempPrefix option but offering a more fine grained
     * control on the naming of the temporary filename.
     * 
     * @param tempFileName
     *            the temporary file name
     * @return a reference to this Camel File binding model
     */
    public CamelFileProducerBindingModel setTempFileName(String tempFileName);

    /**
     * Camel 2.2: Will keep the last modified timestamp from the source file (if
     * any).
     * 
     * @return true to keep the last modified timestamp; false otherwise
     */
    public Boolean isKeepLastModified();

    /**
     * Camel 2.2: Will keep the last modified timestamp from the source file (if
     * any).
     * 
     * @param keepLastModified
     *            whether to keep the last modified timestamp (true), or not
     *            (false)
     * @return a reference to this Camel File binding model
     */
    public CamelFileProducerBindingModel setKeepLastModified(
            Boolean keepLastModified);

    /**
     * Camel 2.3: Whether or not to eagerly delete any existing target file.
     * 
     * @return true if eagerly delete existing target file; false otherwise
     */
    public Boolean isEagerDeleteTargetFile();

    /**
     * Camel 2.3: Whether or not to eagerly delete any existing target file.
     * 
     * @param eagerDeleteTargetFile
     *            true if eagerly delete existing target file; false otherwise
     * @return a reference to this Camel File binding model
     */
    public CamelFileProducerBindingModel setEagerDeleteTargetFile(
            Boolean eagerDeleteTargetFile);

    /**
     * Camel 2.6: If provided, then Camel will write a 2nd done file when the
     * original file has been written.
     * 
     * @return the file name to use
     */
    public String getDoneFileName();

    /**
     * If provided, then Camel will write a 2nd done file when the original file
     * has been written.
     * 
     * @param doneFileName
     *            the file name to use
     * @return a reference to this Camel File binding model
     */
    public CamelFileProducerBindingModel setDoneFileName(String doneFileName);

}
