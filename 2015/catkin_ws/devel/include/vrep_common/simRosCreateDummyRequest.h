// Generated by gencpp from file vrep_common/simRosCreateDummyRequest.msg
// DO NOT EDIT!


#ifndef VREP_COMMON_MESSAGE_SIMROSCREATEDUMMYREQUEST_H
#define VREP_COMMON_MESSAGE_SIMROSCREATEDUMMYREQUEST_H


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
struct simRosCreateDummyRequest_
{
  typedef simRosCreateDummyRequest_<ContainerAllocator> Type;

  simRosCreateDummyRequest_()
    : size(0.0)
    , colors()  {
    }
  simRosCreateDummyRequest_(const ContainerAllocator& _alloc)
    : size(0.0)
    , colors(_alloc)  {
    }



   typedef float _size_type;
  _size_type size;

   typedef std::vector<int8_t, typename ContainerAllocator::template rebind<int8_t>::other >  _colors_type;
  _colors_type colors;




  typedef boost::shared_ptr< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> const> ConstPtr;

}; // struct simRosCreateDummyRequest_

typedef ::vrep_common::simRosCreateDummyRequest_<std::allocator<void> > simRosCreateDummyRequest;

typedef boost::shared_ptr< ::vrep_common::simRosCreateDummyRequest > simRosCreateDummyRequestPtr;
typedef boost::shared_ptr< ::vrep_common::simRosCreateDummyRequest const> simRosCreateDummyRequestConstPtr;

// constants requiring out of line definition



template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> & v)
{
ros::message_operations::Printer< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> >::stream(s, "", v);
return s;
}

} // namespace vrep_common

namespace ros
{
namespace message_traits
{



// BOOLTRAITS {'IsFixedSize': False, 'IsMessage': True, 'HasHeader': False}
// {'sensor_msgs': ['/opt/ros/indigo/share/sensor_msgs/cmake/../msg'], 'std_msgs': ['/opt/ros/indigo/share/std_msgs/cmake/../msg'], 'geometry_msgs': ['/opt/ros/indigo/share/geometry_msgs/cmake/../msg'], 'vrep_common': ['/home/carl/LunaBot/2015/catkin_ws/src/vrep_common/msg']}

// !!!!!!!!!!! ['__class__', '__delattr__', '__dict__', '__doc__', '__eq__', '__format__', '__getattribute__', '__hash__', '__init__', '__module__', '__ne__', '__new__', '__reduce__', '__reduce_ex__', '__repr__', '__setattr__', '__sizeof__', '__str__', '__subclasshook__', '__weakref__', '_parsed_fields', 'constants', 'fields', 'full_name', 'has_header', 'header_present', 'names', 'package', 'parsed_fields', 'short_name', 'text', 'types']




template <class ContainerAllocator>
struct IsFixedSize< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> const>
  : FalseType
  { };

template <class ContainerAllocator>
struct IsMessage< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct HasHeader< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> const>
  : FalseType
  { };


template<class ContainerAllocator>
struct MD5Sum< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "8b8d31e0e18b916960ddd10248b87e65";
  }

  static const char* value(const ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator>&) { return value(); }
  static const uint64_t static_value1 = 0x8b8d31e0e18b9169ULL;
  static const uint64_t static_value2 = 0x60ddd10248b87e65ULL;
};

template<class ContainerAllocator>
struct DataType< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "vrep_common/simRosCreateDummyRequest";
  }

  static const char* value(const ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator>&) { return value(); }
};

template<class ContainerAllocator>
struct Definition< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "\n\
\n\
\n\
\n\
float32 size\n\
int8[] colors\n\
";
  }

  static const char* value(const ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator>&) { return value(); }
};

} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

  template<class ContainerAllocator> struct Serializer< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> >
  {
    template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
    {
      stream.next(m.size);
      stream.next(m.colors);
    }

    ROS_DECLARE_ALLINONE_SERIALIZER;
  }; // struct simRosCreateDummyRequest_

} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const ::vrep_common::simRosCreateDummyRequest_<ContainerAllocator>& v)
  {
    s << indent << "size: ";
    Printer<float>::stream(s, indent + "  ", v.size);
    s << indent << "colors[]" << std::endl;
    for (size_t i = 0; i < v.colors.size(); ++i)
    {
      s << indent << "  colors[" << i << "]: ";
      Printer<int8_t>::stream(s, indent + "  ", v.colors[i]);
    }
  }
};

} // namespace message_operations
} // namespace ros

#endif // VREP_COMMON_MESSAGE_SIMROSCREATEDUMMYREQUEST_H
