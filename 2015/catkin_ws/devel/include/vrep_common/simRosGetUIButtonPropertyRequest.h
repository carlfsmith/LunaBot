// Generated by gencpp from file vrep_common/simRosGetUIButtonPropertyRequest.msg
// DO NOT EDIT!


#ifndef VREP_COMMON_MESSAGE_SIMROSGETUIBUTTONPROPERTYREQUEST_H
#define VREP_COMMON_MESSAGE_SIMROSGETUIBUTTONPROPERTYREQUEST_H


#include <string>
#include <vector>
#include <map>

#include <ros/types.h>
#include <ros/serialization.h>
#include <ros/builtin_message_traits.h>
#include <ros/message_operations.h>


namespace vrep_common
{
template <class ContainerAllocator>
struct simRosGetUIButtonPropertyRequest_
{
  typedef simRosGetUIButtonPropertyRequest_<ContainerAllocator> Type;

  simRosGetUIButtonPropertyRequest_()
    : uiHandle(0)
    , buttonID(0)  {
    }
  simRosGetUIButtonPropertyRequest_(const ContainerAllocator& _alloc)
    : uiHandle(0)
    , buttonID(0)  {
    }



   typedef int32_t _uiHandle_type;
  _uiHandle_type uiHandle;

   typedef int32_t _buttonID_type;
  _buttonID_type buttonID;




  typedef boost::shared_ptr< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> const> ConstPtr;

}; // struct simRosGetUIButtonPropertyRequest_

typedef ::vrep_common::simRosGetUIButtonPropertyRequest_<std::allocator<void> > simRosGetUIButtonPropertyRequest;

typedef boost::shared_ptr< ::vrep_common::simRosGetUIButtonPropertyRequest > simRosGetUIButtonPropertyRequestPtr;
typedef boost::shared_ptr< ::vrep_common::simRosGetUIButtonPropertyRequest const> simRosGetUIButtonPropertyRequestConstPtr;

// constants requiring out of line definition



template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> & v)
{
ros::message_operations::Printer< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> >::stream(s, "", v);
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
struct IsFixedSize< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct HasHeader< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> const>
  : FalseType
  { };


template<class ContainerAllocator>
struct MD5Sum< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "3237fb7c1b11a9bf71b5bb80da60a11a";
  }

  static const char* value(const ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator>&) { return value(); }
  static const uint64_t static_value1 = 0x3237fb7c1b11a9bfULL;
  static const uint64_t static_value2 = 0x71b5bb80da60a11aULL;
};

template<class ContainerAllocator>
struct DataType< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "vrep_common/simRosGetUIButtonPropertyRequest";
  }

  static const char* value(const ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator>&) { return value(); }
};

template<class ContainerAllocator>
struct Definition< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "\n\
\n\
\n\
\n\
int32 uiHandle\n\
int32 buttonID\n\
";
  }

  static const char* value(const ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator>&) { return value(); }
};

} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

  template<class ContainerAllocator> struct Serializer< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> >
  {
    template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
    {
      stream.next(m.uiHandle);
      stream.next(m.buttonID);
    }

    ROS_DECLARE_ALLINONE_SERIALIZER;
  }; // struct simRosGetUIButtonPropertyRequest_

} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const ::vrep_common::simRosGetUIButtonPropertyRequest_<ContainerAllocator>& v)
  {
    s << indent << "uiHandle: ";
    Printer<int32_t>::stream(s, indent + "  ", v.uiHandle);
    s << indent << "buttonID: ";
    Printer<int32_t>::stream(s, indent + "  ", v.buttonID);
  }
};

} // namespace message_operations
} // namespace ros

#endif // VREP_COMMON_MESSAGE_SIMROSGETUIBUTTONPROPERTYREQUEST_H
