// Generated by gencpp from file vrep_common/simRosReadForceSensorResponse.msg
// DO NOT EDIT!


#ifndef VREP_COMMON_MESSAGE_SIMROSREADFORCESENSORRESPONSE_H
#define VREP_COMMON_MESSAGE_SIMROSREADFORCESENSORRESPONSE_H


#include <string>
#include <vector>
#include <map>

#include <ros/types.h>
#include <ros/serialization.h>
#include <ros/builtin_message_traits.h>
#include <ros/message_operations.h>

#include <geometry_msgs/Vector3.h>
#include <geometry_msgs/Vector3.h>

namespace vrep_common
{
template <class ContainerAllocator>
struct simRosReadForceSensorResponse_
{
  typedef simRosReadForceSensorResponse_<ContainerAllocator> Type;

  simRosReadForceSensorResponse_()
    : result(0)
    , force()
    , torque()  {
    }
  simRosReadForceSensorResponse_(const ContainerAllocator& _alloc)
    : result(0)
    , force(_alloc)
    , torque(_alloc)  {
    }



   typedef int32_t _result_type;
  _result_type result;

   typedef  ::geometry_msgs::Vector3_<ContainerAllocator>  _force_type;
  _force_type force;

   typedef  ::geometry_msgs::Vector3_<ContainerAllocator>  _torque_type;
  _torque_type torque;




  typedef boost::shared_ptr< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> const> ConstPtr;

}; // struct simRosReadForceSensorResponse_

typedef ::vrep_common::simRosReadForceSensorResponse_<std::allocator<void> > simRosReadForceSensorResponse;

typedef boost::shared_ptr< ::vrep_common::simRosReadForceSensorResponse > simRosReadForceSensorResponsePtr;
typedef boost::shared_ptr< ::vrep_common::simRosReadForceSensorResponse const> simRosReadForceSensorResponseConstPtr;

// constants requiring out of line definition



template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> & v)
{
ros::message_operations::Printer< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> >::stream(s, "", v);
return s;
}

} // namespace vrep_common

namespace ros
{
namespace message_traits
{



// BOOLTRAITS {'IsFixedSize': True, 'IsMessage': True, 'HasHeader': False}
// {'sensor_msgs': ['/opt/ros/indigo/share/sensor_msgs/cmake/../msg'], 'std_msgs': ['/opt/ros/indigo/share/std_msgs/cmake/../msg'], 'geometry_msgs': ['/opt/ros/indigo/share/geometry_msgs/cmake/../msg'], 'vrep_common': ['/home/carl/LunaBot/2015/catkin_ws/src/vrep_common/msg']}

// !!!!!!!!!!! ['__class__', '__delattr__', '__dict__', '__doc__', '__eq__', '__format__', '__getattribute__', '__hash__', '__init__', '__module__', '__ne__', '__new__', '__reduce__', '__reduce_ex__', '__repr__', '__setattr__', '__sizeof__', '__str__', '__subclasshook__', '__weakref__', '_parsed_fields', 'constants', 'fields', 'full_name', 'has_header', 'header_present', 'names', 'package', 'parsed_fields', 'short_name', 'text', 'types']




template <class ContainerAllocator>
struct IsFixedSize< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct HasHeader< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> const>
  : FalseType
  { };


template<class ContainerAllocator>
struct MD5Sum< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> >
{
  static const char* value()
  {
    return "5e4b65925af0e441033ad70b707ce684";
  }

  static const char* value(const ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator>&) { return value(); }
  static const uint64_t static_value1 = 0x5e4b65925af0e441ULL;
  static const uint64_t static_value2 = 0x033ad70b707ce684ULL;
};

template<class ContainerAllocator>
struct DataType< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> >
{
  static const char* value()
  {
    return "vrep_common/simRosReadForceSensorResponse";
  }

  static const char* value(const ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator>&) { return value(); }
};

template<class ContainerAllocator>
struct Definition< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> >
{
  static const char* value()
  {
    return "int32 result\n\
geometry_msgs/Vector3 force\n\
geometry_msgs/Vector3 torque\n\
\n\
\n\
================================================================================\n\
MSG: geometry_msgs/Vector3\n\
# This represents a vector in free space. \n\
\n\
float64 x\n\
float64 y\n\
float64 z\n\
";
  }

  static const char* value(const ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator>&) { return value(); }
};

} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

  template<class ContainerAllocator> struct Serializer< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> >
  {
    template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
    {
      stream.next(m.result);
      stream.next(m.force);
      stream.next(m.torque);
    }

    ROS_DECLARE_ALLINONE_SERIALIZER;
  }; // struct simRosReadForceSensorResponse_

} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const ::vrep_common::simRosReadForceSensorResponse_<ContainerAllocator>& v)
  {
    s << indent << "result: ";
    Printer<int32_t>::stream(s, indent + "  ", v.result);
    s << indent << "force: ";
    s << std::endl;
    Printer< ::geometry_msgs::Vector3_<ContainerAllocator> >::stream(s, indent + "  ", v.force);
    s << indent << "torque: ";
    s << std::endl;
    Printer< ::geometry_msgs::Vector3_<ContainerAllocator> >::stream(s, indent + "  ", v.torque);
  }
};

} // namespace message_operations
} // namespace ros

#endif // VREP_COMMON_MESSAGE_SIMROSREADFORCESENSORRESPONSE_H
