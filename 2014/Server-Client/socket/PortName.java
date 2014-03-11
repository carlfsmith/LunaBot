/*
 * Purpose: Enumeration used to specify the names of ports
 * Author:  Alex Anderson
 * Notes:   The name fields in port_map.csv must match one of these
 *              names otherwise the port will not be initialized.
 *              Capitalization does not matter in port_map.csv
 *          In/out is also specified in port_map.csv.
 * Date:    3/9/14
 */

package socket;

public enum PortName
{
    GYRO, ACCEL,
    CAM_ANG, CAM_ERROR
}
