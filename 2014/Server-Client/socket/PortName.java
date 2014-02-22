/*
 * Purpose: Enumeration used to specify the names of individual ports
 * Author:  Alex Anderson
 * Notes:   The name fields in port_map.csv must match one of these
 *              names otherwise the port will not be initialized.
 *              Capitalization does not matter in port_map.csv
 */

package socket;

public enum PortName
{
    GYRO_OUT, GYRO_IN, CAMERA
}
