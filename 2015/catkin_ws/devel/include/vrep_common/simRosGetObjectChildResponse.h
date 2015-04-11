// Generated by gencpp from file vrep_common/simRosGetObjectChildResponse.msg
// DO NOT EDIT!


#ifndef VREP_COMMON_MESSAGE_SIMROSGETOBJECTCHILDRESPONSE_H
#define VREP_COMMON_MESSAGE_SIMROSGETOBJECTCHILDRESPONSE_H


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
struct simRosGetObjectChildResponse_
{
  typedef simRosGetObjectChildResponse_<ContainerAllocator> Type;

  simRosGetObjectChildResponse_()
    : childHandle(0)  {
    }
  simRosGetObjectChildResponse_(const ContainerAllocator& _alloc)
    : childHandle(0)  {
    }



   typedef int32_t _childHandle_type;
  _childHandle_type childHandle;




  typedef boost::shared_ptr< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> const> ConstPtr;

}; // struct simRosGetObjectChildResponse_

typedef ::vrep_common::simRosGetObjectChildResponse_<std::allocator<void> > simRosGetObjectChildResponse;

typedef boost::shared_ptr< ::vrep_common::simRosGetObjectChildResponse > simRosGetObjectChildResponsePtr;
typedef boost::shared_ptr< ::vrep_common::simRosGetObjectChildResponse const> simRosGetObjectChildResponseConstPtr;

// constants requiring out of line definition



template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> & v)
{
ros::message_operations::Printer< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> >::stream(s, "", v);
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
struct IsFixedSize< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct HasHeader< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> const>
  : FalseType
  { };


template<class ContainerAllocator>
struct MD5Sum< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> >
{
  static const char* value()
  {
    return "efe17144606e0d5454d7698cb2bf24b7";
  }

  static const char* value(const ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator>&) { return value(); }
  static const uint64_t static_value1 = 0xefe17144606e0d54ULL;
  static const uint64_t static_value2 = 0x54d7698cb2bf24b7ULL;
};

template<class ContainerAllocator>
struct DataType< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> >
{
  static const char* value()
  {
    return "vrep_common/simRosGetObjectChildResponse";
  }

  static const char* value(const ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator>&) { return value(); }
};

template<class ContainerAllocator>
struct Definition< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> >
{
  static const char* value()
  {
    return "int32 childHandle\n\
\n\
";
  }

  static const char* value(const ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator>&) { return value(); }
};

} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

  template<class ContainerAllocator> struct Serializer< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> >
  {
    template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
    {
      stream.next(m.childHandle);
    }

    ROS_DECLARE_ALLINONE_SERIALIZER;
  }; // struct simRosGetObjectChildResponse_

} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const ::vrep_common::simRosGetObjectChildResponse_<ContainerAllocator>& v)
  {
    s << indent << "childHandle: ";
    Printer<int32_t>::stream(s, indent + "  ", v.childHandle);
  }
};

} // namespace message_operations
} // namespace ros

#endif // VREP_COMMON_MESSAGE_SIMROSGETOBJECTCHILDRESPONSE_H
